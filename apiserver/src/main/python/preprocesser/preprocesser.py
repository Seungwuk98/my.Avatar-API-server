# %%
import cv2
import argparse

# haarcascade 불러오기
face_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + 'haarcascade_frontalface_default.xml')
parser = argparse.ArgumentParser('Preprocess Argparse')
parser.add_argument('-b', '--beforedir',
                    help='before preprocess',
                    type=str,
                    default='C:/workspace/avatar/apiserver/src/main/resources/image-process/before')
parser.add_argument('-a', '--afterdir',
                    help='after preprocess',
                    type=str,
                    default='C:/workspace/avatar/apiserver/src/main/resources/image-process/after')
parser.add_argument('-i', '--id',
                    help='uuid of this thread',
                    type=str,
                    default='face')

args = parser.parse_args()


BEFORE_DIR = args.beforedir
AFTER_DIR = args.afterdir
ID = args.id


# 이미지 불러오기
img = cv2.imread('{}/{}.jpg'.format(BEFORE_DIR, ID))
gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)

# 얼굴 찾기
try :
    faces = face_cascade.detectMultiScale(gray, 1.3, 5)
except :
    exit(1)

if len(faces) >= 2:
    exit(2)

for (x, y, w, h) in faces:
    # img = cv2.rectangle(img, (x, y), (x + w, y + h), (255, 0, 0), 2)
		# 이미지 자르기
    print(x, y, w, h)
    cx = x + w // 2
    cy = y + h // 3 
    ww = y + h - cy
    cy = y + h // 2
    print(cy-ww, cy+ww, cx-ww, cx+ww)
    cropped_img = img[cy-ww:cy+ww, cx-ww:cx+ww]
    cropped_img = cv2.resize(cropped_img, dsize=(512, 512))

# 영상 출력
# cv2.imshow('image', img)

# key = cv2.waitKey(0)
# cv2.destroyAllWindows()

# 영상 저장
cv2.imwrite('{}/{}.jpg'.format(AFTER_DIR, ID), cropped_img)
# %%
