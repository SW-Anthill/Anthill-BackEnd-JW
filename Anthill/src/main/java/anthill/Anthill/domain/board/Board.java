package anthill.Anthill.domain.board;

import anthill.Anthill.domain.member.Member;
import anthill.Anthill.dto.board.BoardPagingDTO;
import anthill.Anthill.dto.board.BoardResponseDTO;
import anthill.Anthill.dto.member.MemberResponseDTO;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.*;

@Table(name = "board")
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicUpdate
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "content", nullable = false)
    private String content;

    @Column(name = "writer", nullable = false, length = 20)
    private String writer;

    @Column(name = "hits")
    private Long hits;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder
    public Board(Long id, String title, String content, String writer, Long hits) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.writer = writer;
        this.hits = hits;
    }

    public void setMember(Member member) {
        if (this.member != null) {
            this.member.getBoards()
                       .remove(this);
        }
        this.member = member;
        member.getBoards()
              .add(this);
    }

    public void updateBoard(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void deleteMember() {
        if (this.member != null) {
            this.member.getBoards()
                       .remove(this);
        }
    }

    public void changeInfo(String changedTitle, String changedContent) {
        this.title = changedTitle;
        this.content = changedContent;
    }

    public BoardResponseDTO toBoardResponseDTO() {
        return BoardResponseDTO.builder()
                               .id(this.id)
                               .title(this.title)
                               .content(this.content)
                               .writer(this.writer)
                               .hits(this.hits)
                               .build();
    }

    public void increaseHits() {
        this.hits = this.hits++;
    }

    public BoardPagingDTO toBoardPagingDTO(Board board) {
        return BoardPagingDTO.builder()
                             .id(board.id)
                             .title(board.title)
                             .content(board.content)
                             .writer(board.writer)
                             .hits(board.hits)
                             .build();
    }
}
