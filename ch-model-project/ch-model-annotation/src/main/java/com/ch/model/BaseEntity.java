package com.ch.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;
import java.sql.Timestamp;

@MappedSuperclass
@Data
@EqualsAndHashCode(of = "id")
public abstract class BaseEntity implements Serializable {


    /**
     *
     */
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "created_time")
    private Timestamp createdTime;


    @Column(name = "created_by_id")
    private Long createdById;

    @Column(name = "updated_time")
    private Timestamp updatedTime;


    @Column(name = "updated_by_id")
    private Long updatedById;


}