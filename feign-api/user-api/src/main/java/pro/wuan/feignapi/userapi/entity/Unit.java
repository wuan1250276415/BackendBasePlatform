package pro.wuan.feignapi.userapi.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Table(name = "unit")
@Entity
public class Unit extends BaseEntity {

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", length = Integer.MAX_VALUE)
    private String description;

    @Column(name = "parent_unit_id")
    private Integer parentUnitId;

    @Column(name = "leader_id")
    private Integer leaderId;

    @NotNull
    @Column(name = "status", nullable = false)
    private Boolean status = false;


}