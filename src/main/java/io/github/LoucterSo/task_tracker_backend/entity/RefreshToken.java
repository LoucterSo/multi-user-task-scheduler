//package io.github.LoucterSo.task_tracker_backend.entity;
//
//import jakarta.persistence.*;
//import lombok.*;
//import org.hibernate.annotations.CreationTimestamp;
//
//import java.sql.Timestamp;
//
//// МОЖЕТ В БУДУЩЕМ СДЕЛАЮ СОХРНЕНИЯ В БАЗУ
//@Entity
//@Table(name = "refresh_tokens")
//@Data
//@Builder
//@AllArgsConstructor
//@NoArgsConstructor
//@EqualsAndHashCode(of = "id")
//public class RefreshToken {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    @Column(name = "refresh_token_id")
//    private Long id;
//
//    @Column(name = "refresh_token", unique = true, nullable = false)
//    private String refreshToken;
//
//    @OneToOne
//    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
//    private User user;
//
//    @Column(name = "user_agent")
//    private String userAgent;
//
//    @Column(name = "ip_address")
//    private String ipAddress;
//
//    @CreationTimestamp
//    @Column(name = "created_time", nullable = false)
//    private Timestamp created;
//
//    @Column(name = "expiration_time", nullable = false)
//    private Timestamp expiration;
//
//    @Column(name = "last_used")
//    private Timestamp lastUsed;
//
//    @Column(nullable = false)
//    private boolean revoked = false;
//}
