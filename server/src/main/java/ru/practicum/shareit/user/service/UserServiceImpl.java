package ru.practicum.shareit.user.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeption.BadRequestException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        List<User> userList = repository.findAll().stream()
                               .filter(user -> user.getEmail().equals(userDto.getEmail())).collect(Collectors.toList());
        if (!userList.isEmpty()) {
            logger.error("user found with email={}",userDto.getEmail());
            throw new BadRequestException("User found with email= " + userDto.getEmail());
        }
        logger.info("User save: {}", userDto);
        return UserMapper.toUserDto(repository.save(UserMapper.toUser(userDto)));
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        Optional<User> optionalUser = repository.findById(userId);
        if (optionalUser.isEmpty()) {
            throw new NotFoundException(String.format("user %s not found", userId));
        }
        User userDb = optionalUser.get();
        userDto.setId(userDb.getId());
        setNameAndEmailUser(userDb,userDto);
        repository.save(userDb);
        logger.info("User update: {}", userDb);
        return UserMapper.toUserDto(userDb);
    }

    @Override
    public UserDto getUser(Long userId) {
        Optional<User> user = repository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException(String.format("user %s not found", userId));
        }
        logger.info("User get by id: {}", userId);
        return UserMapper.toUserDto(user.get());
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> userList = repository.findAll();
        if (userList.isEmpty()) {
            throw new NotFoundException(String.format("users %s not found", userList));
        }
        logger.info("Get all users: {}", userList);
        return userList.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public void delete(Long userId) {
        getUser(userId);
        repository.deleteById(userId);
        logger.info("Delete user : {}", userId);
    }

    private void setNameAndEmailUser(User user, UserDto userDto) {
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        logger.info("Set parameters user : {}", user);
    }
}
