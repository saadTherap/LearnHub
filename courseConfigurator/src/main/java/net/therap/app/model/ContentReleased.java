package net.therap.app.model;//package net.therap.app.model;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
///**
// * @author gazizafor
// * @since 24/7/25
// */
//@Entity
//@Table(name = "test_learnhub_content_release")
//@Inheritance(strategy = InheritanceType.JOINED)
//@DiscriminatorColumn(name = "content_type", discriminatorType = DiscriminatorType.STRING)
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
//public abstract class ContentReleased {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "test_learnhub_content_release_seq_gen")
//    // New sequence for Content_Release ID
//    @SequenceGenerator(name = "test_learnhub_content_release_seq_gen", sequenceName =
//            "test_learnhub_content_release_seq", allocationSize = 1)
//    private Long id; // Primary key for this specific content release record
//
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "content_id", referencedColumnName = "id", nullable = false) // FK to the logical Content unit
//    private Content content;
//
//    @Column(name = "`release`", nullable = false) // The version number of this content release
//    private Long release;
//
//    @Column(name = "release_end")
//    private Long releaseEnd;
//
//}