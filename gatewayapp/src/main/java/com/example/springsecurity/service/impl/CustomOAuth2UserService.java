//package com.example.springsecurity.service.impl;
//
//import com.example.springsecurity.entity.User;
//import com.example.springsecurity.repository.UserRepository;
//import lombok.AllArgsConstructor;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
//import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
//import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
//import org.springframework.security.oauth2.core.user.OAuth2User;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Map;
//
//@Service
//@AllArgsConstructor
//public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
//
//private final UserRepository userRepository;
//
//    @Override
//    public OAuth2User loadUser(OAuth2UserRequest userRequest) {
//        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
//        OAuth2User oAuth2User = delegate.loadUser(userRequest);
//
//        Map<String, Object> attributes = oAuth2User.getAttributes();
//        String email = (String) attributes.get("email");
//        String oauth2Id = (String) attributes.get("sub"); // ID пользователя от OAuth2 провайдера
//
//        // Проверка существования пользователя
//        User user = userRepository.findByEmail(email);
//        if (user == null) {
//            // Создание нового пользователя
//            user = new User(email, email, oauth2Id, Role.USER); // username и email совпадают
//            userRepository.save(user);
//            Customer customer = new Customer();
//            customer.setName((String) attributes.get("name")); // Имя пользователя
//            customer.setUser(user);
//            customerRepository.save(customer);
//        } else if (user.getOauth2Id() == null) {
//            // Обновление существующего пользователя с OAuth2 ID, если он отсутствует
//            user.setOauth2Id(oauth2Id);
//            userRepository.save(user);
//        }
//}
