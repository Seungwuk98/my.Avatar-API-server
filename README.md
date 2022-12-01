# SW Maestro project - backend
| | |
| --- | --- |
| 일시 | 2022.06 - 2022.11|
| 팀 | 성은이가 GAN다 |
| 팀원 | 성무열, 은승욱 |
| **backend** | **은승욱** |
| frontend | 은승욱 |
| infra | 은승욱 |
| AI | 성무열 |

**framework** - <img src="https://img.shields.io/badge/Spring boot-white?style=flat-square&logo=Spring boot&logoColor=#6DB33F"/>

---



## API 
base hostname : myavatar.co.kr/api


| Method | URI | Description |
| --- | --- | --- |
| POST | /inference | 사진과 스타일 코드, 성별을 제공하면, 사진에서 얼굴만 추출하여 전처리하고 inference를 진행합니다. 정상적으로 완료되면 사용자 id를 전달합니다. |
| POST | /combine-avatar | 사용자 ID, 스타일 정보, 신체 번호, 머리(hair) 번호를 제공하면, 등록된 머리(head)와 신체, 머리를 합성하여 아바타를 생성한 뒤, 다시 사용자 ID를 반환합니다. |
| GET | /avatar | 사용자 ID를 제공하면, 해당 ID가 유효한 경우 아바타에 접근 가능한 url을 제공합니다.  |
| GET | /toonify | 사용자 ID를 제공하면, 해당 ID가 유효한 경우 스타일이 변환된 이미지를 제공합니다. |
| GET | /bodys | 성별을 제공하면, my.avatar에서 제공하는 모든 신체에 관한 정보를 받을 수 있습니다. |
| GET | /hairs | 성별을 제공하면, my.avatar에서 제공하는 모든 머리(Hair)에 관한 정보를 받을 수 있습니다.  |
| GET | /style | my.avatar에서 제공하는 모든 스타일에 관한 정보를 얻을 수 있습니다.  |
| GET | /sexuality | 사용자 ID를 제공하면, ID에 해당하는 성별을 얻을 수 있습니다.  |

[상세 명세서](https://kindly-tumble-1e1.notion.site/API-8eb854bf5d844f6497897d31c22c8478)
## Class Diagram
<img src="project.png" style="background:white; padding:10px">

