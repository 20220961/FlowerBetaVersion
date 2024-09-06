<h1>게시글 쓰기 기능</h1><br>
9/2<br>
사진 올리기<br>
CMPostForm과 CMPicture로 분리<br>
글 수정하기 기능 추가<br>
글 삭제 (비활성화) 기능 추가 예정<br>
9/3<br>
글 삭제 (비활성화) 기능 추가<br>
비활성화 된 글에 접근시 게시글 메인페이지로 리다이렉트<br>
9/4<br>
PostMapping(/new) -> PostMapping(/create)로 변경<br>
PutMapping(/disable) -> PutMapping(/delete)로 변경<br>

<h2>게시글 쓰기 DB</h2>
9/3<br>
필드에 disable항목 추가<br>
9/6<br>
졸작테이블에 게시글 쓰기 관련 DB 업데이트 완료<br>

<h1>댓글 달기 기능</h1><br>
9/2<br>
댓글에 답글 > 답글에 답글 > 답글에 달린 답글에 답글 식으로<br>
댓글 달기 기능 수정<br>
9/3<br>
댓글 수정 기능 추가 (현재 수정중)<br>
댓글 삭제 (비활성화) 기능 추가 예정<br>
9/4<br>
댓글 삭제 (비활성화) 기능 추가<br>
PostMapping(/new) -> PostMapping(/create)로 변경<br>
PostMapping(/{id}/new) -> PostMapping(/{id}/create)로 변경<br>
PutMapping(/disable) -> PutMapping(/delete)로 변경<br>

<h2>댓글 달기 DB</h2><br>
9/2<br>
CMRecomment 테이블 삭제<br>
CMComment 테이블에 parant_comment_id 추가<br>
9/6<br>
졸작테이블에 댓글 달기 관련 DB 업데이트 완료<br>

<h1>다이어리 기능</h1>
9/6<br>
다이어리 작성기능 << 제목, 내용, 날짜, 공개or비공개 여부 <br>
다이어리 수정기능<br>
다이어리 삭제(비공개 기능) api화 완료<br>
현재 mustache에서는 내용이 안보이는 현상이있음 << Api tester로 테스트<br>
mustache에서 테스트는 추후 업데이트 할 예정<br>
다이어리 에서 isPublic을 0으로 설정시 작성자만 볼 수 있음<br>
다이어리 에서 isPublic을 1로 설정시 모든이가 볼 수 있음<br>

<h2>다이어리 DB</h2>
9/6<br>
졸작테이블에 다이어리 관련DB 업데이트 완료<br>

<h1>추가 수정 내용</h1>
9/2<br>
CMRecomment 사용하는 guestbook 임시삭제
