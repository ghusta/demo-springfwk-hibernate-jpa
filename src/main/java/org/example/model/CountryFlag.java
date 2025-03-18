package org.example.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "country_flag")
public class CountryFlag {
    @Id
    @Size(max = 2)
    @Comment("Following ISO 3166-1 alpha-2 code")
    @Column(name = "code2", nullable = false, length = 2)
    private String code2;

    @NotNull
    @Comment("Source: Emoji flag symbols (https://apps.timwhitlock.info/emoji/tables/iso3166)")
    @Column(name = "emoji", nullable = false, length = Integer.MAX_VALUE)
    private String emoji;

    @Column(name = "unicode", length = Integer.MAX_VALUE)
    private String unicode;

    public String getCode2() {
        return code2;
    }

    public void setCode2(String code2) {
        this.code2 = code2;
    }

    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }

    public String getUnicode() {
        return unicode;
    }

    public void setUnicode(String unicode) {
        this.unicode = unicode;
    }

}