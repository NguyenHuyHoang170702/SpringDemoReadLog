package com.spring.catch_error_service.entity;


import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "errors_format")
public class ErrorFormat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(nullable = true)
    private String requestId;
    @Column(nullable = true)
    private String url;
    @Column(nullable = true)
    private String method;
    @Column(length = 5000)
    private String jsonData;
    private boolean isRetry;
    private boolean isPush;
    private Date createDate;
    private Date updateDate;
}
