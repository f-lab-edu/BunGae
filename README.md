## BunGae

BunGae 앱은 다양한 활동을 다양한 사람들과 급 번개 모임을 가질 수 있도록 지원하는 앱입니다.
<br>
<br>
<br>

### 순서도
----------
<!-- ![1](https://user-images.githubusercontent.com/72846127/225082721-b15b8f99-69de-463e-8500-31ac636ed7d5.png)
<br>
![2](https://user-images.githubusercontent.com/72846127/225082730-fa7a640c-8d8d-4948-8061-27961a37ae8f.png)
<br>
![3](https://user-images.githubusercontent.com/72846127/225082736-99b86667-19a7-4a9c-a783-93cb2868cfba.png)
<br>
![4](https://user-images.githubusercontent.com/72846127/225082738-99e614b6-0e21-493b-8e9c-2453109a55de.png)
<br>
![5](https://user-images.githubusercontent.com/72846127/225082740-fc5497a3-4529-4e2e-9bf4-5589aeebe13b.png)
<br>  -->
#### 회원가입
![image](https://user-images.githubusercontent.com/72846127/229263000-0e919a7c-70dc-433e-a6b3-ba03a7f9d501.png)
<br>
#### 게시글 등록
![image](https://user-images.githubusercontent.com/72846127/229263005-a2c80a1b-d5c4-4f08-ac7b-285608241162.png)
<br>
#### 채팅
![image](https://user-images.githubusercontent.com/72846127/229262984-b5ddc00c-bf0d-4448-8d27-333e43600f9d.png)
<br>



<br>
<br>
<br>

### 주요 기술
---------
- 100% 코틀린으로 작성되었습니다.
- ViewModel을 이용해 화면 회전에 대응했습니다.
- DataBinding으로 UI와 데이터를 선언적 형식으로 결합하였습니다.
- LiveData를 이용해 Observer 패턴을 적용했습니다.
- MVVM 패턴으로 작성되었습니다.
- Hilt를 이용해 의존성 주입을 하였습니다.
- Firebase를 이용해 계정 생성, 데이터 및 이미지 저장, 채팅 기능을 구현하였습니다.
- RecyclerView에 ListAdapter를 적용하여 구현하였습니다.
- GoogleMap을 활용해 앱 내부에 지도를 올리고 현재위치, 주소 검색, 다른 사람이 등록한 마커를 볼 수 있습니다.
- Bottom Navigation을 이용해 Fragment 전환을 쉽게 할 수 있습니다.
- Glide 라이브러리를 사용해 이미지 처리를 하고 있습니다.
<br>
<br>
<br>

### UI
--------
#### 회원가입
![Screenshot_20230327_134129_Gallery](https://user-images.githubusercontent.com/72846127/229263145-0738e35a-f49c-4cce-ab47-d92d39a0c912.jpg) 
- 이메일 형식으로 회원가입을 진행합니다.
- 이메일 중복 확인과 비밀번호 일치 여부를 확인하고 값을 넘깁니다.

#### 프로필 등록
![Screenshot_20230327_134147_Gallery](https://user-images.githubusercontent.com/72846127/229263150-449534c5-ee4f-4b86-8e87-1fe82b87155e.jpg) 
![Screenshot_20230327_134159_Gallery](https://user-images.githubusercontent.com/72846127/229263151-0616279f-b7c6-40c1-95db-76e76693b8d2.jpg)

![Screenshot_20230327_134216_Gallery](https://user-images.githubusercontent.com/72846127/229263155-f5bc3d87-1470-44fc-b8bc-1155459d566d.jpg) 
![Screenshot_20230327_134231_Gallery](https://user-images.githubusercontent.com/72846127/229263162-11ecbea4-3aaa-4631-b263-3c8522176618.jpg) 
- 카메라와 내부 저장소의 접근 권한을 확인합니다.
- 닉네임 중복 체크를 하면 Firebase와 통신하여 닉네임 중복 여부를 확인 후 결과를 반환합니다.

#### 로그인
![Screenshot_20230327_134244_Gallery](https://user-images.githubusercontent.com/72846127/229263173-b4e57247-8ee7-4353-9f72-15dc6c20dde4.jpg) 
![Screenshot_20230327_134254_Gallery](https://user-images.githubusercontent.com/72846127/229263215-0c622036-b968-4c55-ae7e-f34a9aa6eee7.jpg)
- 생성한 계정으로 로그인을 합니다.

#### 글 작성
![Screenshot_20230327_134509_Gallery](https://user-images.githubusercontent.com/72846127/229263400-d158ceb7-179f-4b84-ba61-aa271f680fdf.jpg) 
![Screenshot_20230327_134615_Gallery](https://user-images.githubusercontent.com/72846127/229263405-2e4c5da0-28f8-4f1c-82af-2d1557bd2297.jpg)

![Screenshot_20230327_134621_Gallery](https://user-images.githubusercontent.com/72846127/229263409-3d9024c8-b2bd-44af-867e-942ace985ad5.jpg) 
![Screenshot_20230327_134631_Gallery](https://user-images.githubusercontent.com/72846127/229263415-08af24b4-e9bb-4c66-ab58-21ddf780e21d.jpg) 
- 먼저 위치 접근 권한을 확인합니다.
- 글 내용을 입력하고 모임 장소를 구글 맵으로 검색하면 해당 위치로 마커가 이동됩니다.
- '여기에서 봐요!' 버튼을 누르면 해당 주소가 화면에 등록됩니다.

#### 게시글 수정
![Screenshot_20230327_134636_Gallery](https://user-images.githubusercontent.com/72846127/229263516-3da81471-25a9-448d-9125-a76971e38cd8.jpg) 
![Screenshot_20230327_134708_Gallery](https://user-images.githubusercontent.com/72846127/229263519-5892d8eb-2655-4eb6-966c-e38b7b65dce9.jpg) 
![Screenshot_20230327_134721_Gallery](https://user-images.githubusercontent.com/72846127/229263522-f0b4bc49-6810-4d79-ac98-fe8b80f520aa.jpg)
- 게시글 수정을 진행합니다.
- 수정을 완료하면 게시글 수정 액티비티는 종료되고 수정한 내용이 반영되어있는 액티비티를 볼 수 있습니다.

#### 채팅
사용자: 굳건이 &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;  &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 사용자: 너굴

![KakaoTalk_20230328_021623463](https://user-images.githubusercontent.com/72846127/229263998-e512ec5c-799b-4d8a-9ede-6116103e0480.gif) ![KakaoTalk_20230328_021623463_06](https://user-images.githubusercontent.com/72846127/229264354-0180061c-ed14-4b6c-b4f8-907391e72f35.jpg)
- 메시지를 받으면 대화방이 생기는 걸 볼 수 있습니다.

<br><br>

사용자: 굳건이 &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;  &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; 사용자: 너굴

![KakaoTalk_20230328_021623463_01](https://user-images.githubusercontent.com/72846127/229264484-89826696-6aec-492f-b048-099e8f07e9a1.gif) ![KakaoTalk_20230328_021623463_04](https://user-images.githubusercontent.com/72846127/229264415-60723ed4-f97a-4d92-9d65-85195ae7d3e0.gif) ![KakaoTalk_20230328_021623463_03](https://user-images.githubusercontent.com/72846127/229264444-90756003-7b65-41c7-a2a1-333206c8ed52.gif) ![KakaoTalk_20230328_021623463_05](https://user-images.githubusercontent.com/72846127/229264454-297096b2-e9d1-401f-a0f8-83860a75ac88.gif)

- 대화를 주고 받을 때 메세지를 보내면 바로 대화방에 반영되는 걸 볼 수 있습니다.

#### 구글 맵
![KakaoTalk_20230327_162224466](https://user-images.githubusercontent.com/72846127/229264548-dc6df6f5-0b29-4fd7-a939-78d014bddda5.jpg) ![KakaoTalk_20230327_162224466_01](https://user-images.githubusercontent.com/72846127/229264553-6b63c672-7971-4573-a803-72e3a79fe2bc.jpg)
- 맵 프래그먼트를 실행하면 현재 내 위치가 바로 표시됩니다.
- 이전에 올린 게시글의 마커도 표시되어있는 걸 볼 수 있습니다.

#### 마이 페이지
![Screenshot_20230327_134821_Gallery](https://user-images.githubusercontent.com/72846127/229264608-99cee76e-e834-46a9-9b4c-886f17e8aa92.jpg) ![Screenshot_20230327_134826_Gallery](https://user-images.githubusercontent.com/72846127/229264609-59da4fd3-c66c-4f43-900a-e56aec39005a.jpg)

![Screenshot_20230327_134837_Gallery](https://user-images.githubusercontent.com/72846127/229264611-9fe1f0b3-79db-4375-94c9-20d20588c255.jpg) ![Screenshot_20230327_134846_Gallery](https://user-images.githubusercontent.com/72846127/229264612-ba49b656-a44e-4767-b1d1-7fbe6f47bb54.jpg)
- 마이 페이지에 내 프로필 사진을 누르면 사진을 변경할 수 있습니다.
- 닉네임을 변경하면 닉네임 변경하기 버튼은 사라지고 내가 원하는 닉네임을 입력 후 중복 검사에 통과하면 닉네임이 변경됩니다.
- 내가 쓴 글 보기를 누르면 내가 올린 글만 필터링 되어 보여집니다.


