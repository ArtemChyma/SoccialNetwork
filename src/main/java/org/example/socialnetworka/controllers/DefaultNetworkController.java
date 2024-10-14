package org.example.socialnetworka.controllers;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.example.socialnetworka.chats.Chat;
import org.example.socialnetworka.friends.FriendShip;
import org.example.socialnetworka.repository.ChatRepository;
import org.example.socialnetworka.repository.FriendShipRepository;
import org.example.socialnetworka.repository.UserRepository;
import org.example.socialnetworka.repository.UsersChatsRepository;
import org.example.socialnetworka.services.UserService;
import org.example.socialnetworka.services.UserServiceImpl;
import org.example.socialnetworka.users.User;
import org.example.socialnetworka.users.UserChat;
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
    private final UsersChatsRepository usersChatsRepository;
    private final ChatRepository chatRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    @Autowired
    public DefaultNetworkController(UserRepository userRepository, PasswordEncoder passwordEncoder,
                                    UserService userService, FriendShipRepository friendShipRepository,
                                    UsersChatsRepository usersChatsRepository, ChatRepository chatRepository) {
        this.userRepository = userRepository;
        this.friendShipRepository = friendShipRepository;
        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.usersChatsRepository = usersChatsRepository;
        this.chatRepository = chatRepository;
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
        return "redirect:/login?logout=true";
    }

//    @GetMapping("/initPTP")
//    public String initPTP() {
//
//        return "redirect:/profile";
//    }


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
            User userAuth = null;
            if (principal instanceof UserDetails userDetails) {
                userAuth = (User) userRepository.findUserByUsername(userDetails.getUsername());
            } else if (principal instanceof String){
                userAuth = userRepository.findUserByUsername((String) principal);
            }

            if (userAuth.getId().equals(user.getId())) {
                model.addAttribute("Permission", "Approved");
            } else {
                FriendShip friendShipByUser = friendShipRepository.findFriendShipByUserIdAndFriendIdAndStatus(userAuth.getId(), user.getId(), "Accepted");
                FriendShip friendShipByFriend = friendShipRepository.findFriendShipByUserIdAndFriendIdAndStatus(user.getId(), userAuth.getId(), "Accepted");
                FriendShip friendShipRequestByUser = friendShipRepository.findFriendShipByUserIdAndFriendIdAndStatus(userAuth.getId(), user.getId(), "Requested");
                FriendShip friendShipRequestByFriend = friendShipRepository.findFriendShipByUserIdAndFriendIdAndStatus(user.getId(), userAuth.getId(), "Requested");
                if (friendShipByUser != null || friendShipByFriend != null) {
                    model.addAttribute("IsAFriend", "Is a friend");
                } else if (friendShipRequestByUser == null && friendShipRequestByFriend == null) {
                    model.addAttribute("NotAFriend", "Not a friend");
                }
            }

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
//            System.out.println("Content type " + file.getContentType() + " Resource  " + file.getResource() + " Original Name " + file.getOriginalFilename());
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

    @PostMapping("/profile/friendRequest/{id}")
    public String friendRequest(@PathVariable Long id, Principal principal) {
        User user = userRepository.findUserByUsername(principal.getName());
        friendShipRepository.save(new FriendShip(user.getId(), id, "Requested"));
        return "redirect:/profile/" + id;
    }

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
        Chat chat = new Chat(principal.getName() + ',' + userRepository.findUserById(id).getUsername());
        chatRepository.save(chat);
        Long chatId = chatRepository.findChatByName(principal.getName() + ',' + userRepository.findUserById(id).getUsername()).getId();
        usersChatsRepository.save(new UserChat(chatId , id));
        usersChatsRepository.save(new UserChat(chatId, userRepository.findUserByUsername(principal.getName()).getId()));
        return "redirect:/profile/friends";
    }

    private Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    @GetMapping("/profile/chats")
    public String chats(Model model, Principal principal) {

        model.addAttribute("Chats", usersChatsRepository.findAllByUserId(userRepository.findUserByUsername(principal.getName()).getId()));
        model.addAttribute("ChatRepo", chatRepository);
        return "chats";
    }
    
    @GetMapping("/profile/chats/chat/{id}")
    public String chat(@PathVariable("id") Long id, Model model) {

        return "chat";
    }
}
