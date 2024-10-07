package org.example.socialnetworka.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.example.socialnetworka.friends.FriendShip;
import org.example.socialnetworka.repository.FriendShipRepository;
import org.example.socialnetworka.repository.UserRepository;
import org.example.socialnetworka.services.UserService;
import org.example.socialnetworka.services.UserServiceImpl;
import org.example.socialnetworka.users.User;
import org.example.socialnetworka.users.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Controller
public class DefaultNetworkController {

    private final UserRepository userRepository;
    private final FriendShipRepository friendShipRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;


//    private final FileService fileService;
    @Autowired
    public DefaultNetworkController(UserRepository userRepository, PasswordEncoder passwordEncoder,
                                    UserService userService, FriendShipRepository friendShipRepository
//            ,
//                                    FileService fileService
    ) {
        this.userRepository = userRepository;
        this.friendShipRepository = friendShipRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
//        this.fileService = fileService;
    }

    @GetMapping("/login")
    public String login(Model model, UserDAO userDAO) {
        model.addAttribute("user", userDAO);
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model, UserDAO userDAO) {
        model.addAttribute("user", userDAO);
        return "register";
    }

    @PostMapping("/register")
    public String postRegister(Model model, @ModelAttribute("user") @Valid UserDAO userDAO, BindingResult bindingResult) {
        if(bindingResult.hasErrors()){
            return "/register";
        }

        if(userRepository.findUserByEmail(userDAO.getEmail()) != null || userRepository.findUserByUsername(userDAO.getUsername()) != null) {
            model.addAttribute("userExist", "User with such email or username is already exist");
            return "register";
        }

        model.addAttribute("success", "You have successfully registered");
        User user = new User(userDAO.getId(), userDAO.getFirstName(), userDAO.getSecondName(),
                userDAO.getEmail(), passwordEncoder.encode(userDAO.getPassword()), userDAO.getUsername(), null);
        userRepository.save(user);
        return "/register";
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {
        SecurityContextHolder.getContext().getAuthentication().setAuthenticated(false);
        return "redirect:/login";
    }

    @GetMapping("/profile")
    public String profile(Model model) {
        Authentication authentication = getAuthentication();
        Object principal = authentication.getPrincipal();
        System.out.println(authentication.getPrincipal() + "  " + principal.getClass());
        if (principal instanceof UserDetails userDetails) {
            var user = (User) userRepository.findUserByUsername(userDetails.getUsername());
            model.addAttribute("user", user);
        } else if (principal instanceof String){
            User user = userRepository.findUserByUsername((String) principal);
            model.addAttribute(user);
        }

        System.out.println("Has permission");
        model.addAttribute("Permission", "Approved");
        return "profile";
    }

    @GetMapping("/profile/{id}")
    public String particularProfile(Model model, @PathVariable Long id) {
        var user = userRepository.findUserById(id);
        if( user != null){
            model.addAttribute("user", userRepository.findUserById(id));
            Authentication authentication = getAuthentication();
            Object principal = authentication.getPrincipal();
            System.out.println(authentication.getPrincipal() + "  " + principal.getClass());
            User userAuth = null;
            if (principal instanceof UserDetails userDetails) {
                userAuth = (User) userRepository.findUserByUsername(userDetails.getUsername());
            } else if (principal instanceof String){
                userAuth = userRepository.findUserByUsername((String) principal);
            }

            if (userAuth.getId().equals(user.getId())) {
                model.addAttribute("Permission", "Approved");
//                model.addAttribute("hasPermission");
            }
            FriendShip friendShipByUser = friendShipRepository.findFriendShipByUserIdAndFriendIdAndStatus(userAuth.getId(), user.getId(), "Accepted");
            FriendShip friendShipByFriend = friendShipRepository.findFriendShipByUserIdAndFriendIdAndStatus(user.getId(), userAuth.getId(), "Accepted");
            FriendShip friendShipRequestByUser = friendShipRepository.findFriendShipByUserIdAndFriendIdAndStatus(userAuth.getId(), user.getId(), "Requested");
            FriendShip friendShipRequestByFriend = friendShipRepository.findFriendShipByUserIdAndFriendIdAndStatus(user.getId(), userAuth.getId(), "Requested");

            if (friendShipByUser != null || friendShipByFriend != null) {
                model.addAttribute("IsAFriend", "Is a friend");
            } else if (friendShipRequestByUser == null && friendShipRequestByFriend == null) {
                model.addAttribute("NotAFriend", "Not a friend");
            }
//
//            List<FriendShip> friendShips = friendShipRepository.findAllByUserIdAndFriendId(userAuth.getId(), user.getId());
//            if (friendShips.isEmpty()) {
//                friendShips = friendShipRepository.findAllByUserIdAndFriendId(user.getId(), userAuth.getId());
//                if (friendShips.isEmpty()) {
//                    model.addAttribute("NotAFriend", "Not a friend");
//                } else {
//                    model.addAttribute("IsAFriend", "Is a friend");
//                }
//            } else {
//                model.addAttribute("IsAFriend", "Is a friend");
//            }
//            boolean exist = false;
//            if (!friendShips.isEmpty()){
//                for (FriendShip friendShip : friendShips) {
//                    if (friendShip.getUserId().equals(userAuth.getId()) && friendShip.getFriendId().equals(user.getId())) {
//                        exist = true;
//                        break;
//                    }
//
//                }
//                if (!exist) {
//                    model.addAttribute("NotAFriend", "Not a friend");
//                }
//            } else if (!userAuth.getId().equals(user.getId())) {
//                model.addAttribute("NotAFriend", "Not a friend");
//            }
            return "profile";
        }

        return "not_existed_user";
    }

    @GetMapping("/profile/settings")
    public String settings(Model model, Authentication authentication) {
        Object principal = authentication.getPrincipal();
        User user = null;
        if (principal instanceof String) {
            user = userRepository.findUserByUsername((String)principal );
        } else if(principal instanceof UserDetails) {
            var userDetails = (UserDetails)authentication.getPrincipal();
            user = userRepository.findUserByUsername(userDetails.getUsername());
        }
        try {
            UserDAO userDAO = new UserDAO(user.getId(), user.getFirstName(),
                    user.getSecondName(), user.getEmail(), user.getPassword(), user.getUsername(), null);
            model.addAttribute("user", userDAO);
        }catch (NullPointerException e){
            System.out.println("User is null");
        }

        return "settings";
    }

    @PostMapping(path = "/profile/settings", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String editSettingProfile(Model model, @ModelAttribute("user") @Valid UserDAO userDAO, BindingResult bindingResult,
                                     @RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException{

        System.out.println(Arrays.toString(file.getBytes()));
        if (bindingResult.hasErrors()) {
            return "settings";
        }

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof String) {
            if (userRepository.findUserByUsername(userDAO.getUsername()) != null && !principal.equals(userDAO.getUsername())) {
                model.addAttribute("usernameState", "Username is already taken");
                return "settings";
            }
        } else if (principal instanceof UserDetails) {
            if (userRepository.findUserByUsername(userDAO.getUsername()) != null && !((UserDetails) principal).getUsername().equals(userDAO.getUsername())) {
                model.addAttribute("usernameState", "Username is already taken");
                return "settings";
            }
        }

        System.out.println(userDAO.getEmail());
        User user = userRepository.findUserByEmail(userDAO.getEmail());
        if (!file.isEmpty()) {
            String[] imageType = file.getContentType().split("/");
            Files.write(Paths.get("src/main/resources/static/images/" + user.getId() + "." + imageType[1]), file.getBytes());
            userRepository.save(new User(user.getId(), userDAO.getFirstName(), userDAO.getSecondName(), user.getEmail(),
                    user.getPassword(), userDAO.getUsername(), user.getId() + "." + imageType[1]));
        } else {
            userRepository.save(new User(user.getId(), userDAO.getFirstName(), userDAO.getSecondName(), user.getEmail(),
                    user.getPassword(), userDAO.getUsername(), user.getIdImagePath()));
            System.out.println("Content type " + file.getContentType() + " Resource  " + file.getResource() + " Original Name " + file.getOriginalFilename());
        }

        Collection<? extends GrantedAuthority> nowAuthorities =
                SecurityContextHolder.getContext()
                        .getAuthentication()
                        .getAuthorities();
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDAO.getUsername(), user.getPassword(), nowAuthorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return "redirect:/profile";
    }

//    @GetMapping("/profile/friends")
//    public String displayFriends(Model model) {
//        java.util.List<User> listOfUsers= userRepository.findAll();
//        model.addAttribute("users", listOfUsers);
//        return "friends";
//    }

    @PostMapping("/profile/friendRequest/{id}")
    public String friendRequest(@PathVariable Long id, Principal principal) {
        User user = userRepository.findUserByUsername(principal.getName());
        friendShipRepository.save(new FriendShip(user.getId(), id, "Requested"));
        return "redirect:/profile/" + id;
    }

//    @GetMapping("/profile/settings/uploadFile")
//    public String index() {
//        return "upload";
//    }
//
//    @PostMapping(path = "/profile/settings/uploadFile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) throws IOException {
//        System.out.println("Uploading file");
//        fileService.uploadFile(file);
//
//        redirectAttributes.addFlashAttribute("message",
//                "You successfully uploaded " + file.getOriginalFilename() + "!");
//        return "redirect:/profile/settings";
//    }

    @GetMapping("/profile/friends")
    public String showFriends(Model model, Principal principal) {
        User user = userRepository.findUserByUsername(principal.getName());
        List<FriendShip> friendShipRequests = friendShipRepository.findAllByFriendIdAndStatus(user.getId(), "Requested");
        List<FriendShip> acceptedFriendShipsByUser = friendShipRepository.findAllByFriendIdAndStatus(user.getId(),"Accepted");
        List<FriendShip> acceptedFriendShipsByFriend = friendShipRepository.findAllByUserIdAndStatus(user.getId(), "Accepted");
        boolean isAddUserRepo = false;
        if (!friendShipRequests.isEmpty()) {
            model.addAttribute("friendShipRequests", friendShipRequests);
            isAddUserRepo = true;
        }
        if (!acceptedFriendShipsByUser.isEmpty()) {
            model.addAttribute("acceptedFriendShipsByUser", acceptedFriendShipsByUser);
            isAddUserRepo = true;
        }
        if (!acceptedFriendShipsByFriend.isEmpty()) {
            model.addAttribute("acceptedFriendShipsByFriend", acceptedFriendShipsByFriend);
            isAddUserRepo = true;

        }
        if (isAddUserRepo)
            model.addAttribute("userRepo", userRepository);

        return "friends";
    }

    @DeleteMapping("/profile/friends/{id}")
    public String declineFriendShipRequest(@PathVariable Long id, Principal principal) {
        FriendShip friendShip = friendShipRepository.findFriendShipByUserIdAndFriendId(id, userRepository.findUserByUsername(principal.getName()).getId());
        if (friendShip == null) {
            friendShip = friendShipRepository.findFriendShipByUserIdAndFriendId(userRepository.findUserByUsername(principal.getName()).getId(), id);
            friendShipRepository.delete(friendShip);
        } else {
            friendShipRepository.delete(friendShip);
        }

        return "redirect:/profile/friends";
    }

    @PatchMapping("/profile/friends/{id}")
    public String acceptFriendShipRequest(@PathVariable Long id, Principal principal) {
        FriendShip friendShip = friendShipRepository.findFriendShipByUserIdAndFriendId(id, userRepository.findUserByUsername(principal.getName()).getId());
        friendShip.setStatus("Accepted");
        friendShipRepository.save(friendShip);
        return "redirect:/profile/friends";
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
