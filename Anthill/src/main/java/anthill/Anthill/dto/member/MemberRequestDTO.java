package anthill.Anthill.dto.member;

import anthill.Anthill.domain.member.Address;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

public class MemberRequestDTO {
    //@NotNull : Null만 허용하지 않음 "", " "허용
    //@NotEmpty : null, "" 허용하지 않음 " "허용
    //@NotBlank : null, ""," "허용하지 않음
    //@Pattern : 지정된 패턴만 입력하게 하여 휴대폰 번호 폼에서 이상한 값들이 요청되는 것을 방지합니다.

    @NotBlank(message = "아이디를 입력해주세요.")
    @Size(min=5, max=20, message="아이디는 5자 이상 20자 이하로 입력해주세요.")
    private String userId;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min=8, message="비밀번호를 8자 이상으로 입력해주세요.")
    private String password;

    @NotBlank(message = "별명을 입력해주세요.")
    @Size(max=20, message="별명을 20자 이하로 입력해주세요.")
    private String nickName;

    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    @NotBlank(message = "휴대전화번호를 입력해주세요.")
    @Pattern(regexp = "(01[016789])(\\d{3,4})(\\d{4})", message = "올바른 휴대폰 번호를 입력해주세요.")
    private String phoneNumber;

    Address address;

}
