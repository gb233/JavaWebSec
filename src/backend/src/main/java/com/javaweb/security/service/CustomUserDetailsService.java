package com.javaweb.security.service;

import com.javaweb.security.entity.User;
import com.javaweb.security.repository.UserRepository;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 自定义用户详细信息服务
 *
 * @author JavaWeb安全教学系统
 * @since 1.0.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

  private final UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
    log.debug("加载用户详细信息：usernameOrEmail={}", usernameOrEmail);

    User user =
        userRepository
            .findByUsernameOrEmail(usernameOrEmail)
            .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + usernameOrEmail));

    return UserPrincipal.create(user);
  }

  /** 根据用户ID加载用户 */
  @Transactional(readOnly = true)
  public UserDetails loadUserById(Long id) {
    log.debug("根据ID加载用户详细信息：id={}", id);

    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + id));

    return UserPrincipal.create(user);
  }

  /** 用户主体类（实现UserDetails接口） */
  public static class UserPrincipal implements UserDetails {
    private Long id;
    private String username;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private boolean enabled;
    private boolean accountNonLocked;
    private boolean accountNonExpired;
    private boolean credentialsNonExpired;

    public UserPrincipal(
        Long id,
        String username,
        String email,
        String password,
        Collection<? extends GrantedAuthority> authorities,
        boolean enabled,
        boolean accountNonLocked) {
      this.id = id;
      this.username = username;
      this.email = email;
      this.password = password;
      this.authorities = authorities;
      this.enabled = enabled;
      this.accountNonLocked = accountNonLocked;
      this.accountNonExpired = true;
      this.credentialsNonExpired = true;
    }

    public static UserPrincipal create(User user) {
      List<GrantedAuthority> authorities = new ArrayList<>();

      // 添加角色权限
      authorities.add(new SimpleGrantedAuthority("ROLE_" + user.getUserRole().name()));

      // 在统一权限模式下，所有注册用户都有相同的权限
      authorities.add(new SimpleGrantedAuthority("PERM_LEARN_VULNERABILITIES"));
      authorities.add(new SimpleGrantedAuthority("PERM_TAKE_TESTS"));
      authorities.add(new SimpleGrantedAuthority("PERM_COMPLETE_CHALLENGES"));
      authorities.add(new SimpleGrantedAuthority("PERM_VIEW_PROGRESS"));
      authorities.add(new SimpleGrantedAuthority("PERM_MANAGE_PROFILE"));

      boolean enabled = user.getUserStatus() == User.UserStatus.ACTIVE;
      boolean accountNonLocked = !user.isAccountLocked();

      return new UserPrincipal(
          user.getId(),
          user.getUsername(),
          user.getEmail(),
          user.getPasswordHash(),
          authorities,
          enabled,
          accountNonLocked);
    }

    // Getters
    public Long getId() {
      return id;
    }

    public String getEmail() {
      return email;
    }

    @Override
    public String getUsername() {
      return username;
    }

    @Override
    public String getPassword() {
      return password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
      return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
      return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
      return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
      return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
      return enabled;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      UserPrincipal that = (UserPrincipal) o;
      return id.equals(that.id);
    }

    @Override
    public int hashCode() {
      return id.hashCode();
    }
  }
}
