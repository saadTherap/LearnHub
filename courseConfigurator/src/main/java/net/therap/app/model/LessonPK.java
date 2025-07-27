package net.therap.app.model;//package net.therap.app.model;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Getter;
//import lombok.NoArgsConstructor;
//import lombok.Setter;
//
//import java.util.Objects;
//
///**
// * @author gazizafor
// * @since 21/7/25
// */
//@AllArgsConstructor
//@NoArgsConstructor
//@Getter
//@Setter
//@Embeddable
//public class LessonPK {
//
//    @Column(nullable = false)
//    private long id;
//
//    @Column(nullable = false)
//    private long release;
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) {
//            return true;
//        }
//
//        if (o instanceof LessonPK) {
//            LessonPK lessonPK = (LessonPK) o;
//
//            return this.id == lessonPK.id && this.release == lessonPK.release;
//        }
//
//        return false;
//    }
//
//    @Override
//    public String toString() {
//        return "LessonPK{" + "pk=" + id + ", release=" + release + '}';
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(id, release);
//    }
//}