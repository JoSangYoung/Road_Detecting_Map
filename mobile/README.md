# Android YOLO with TensorFlow Mobile
이 안드로이드 애플리케이션은 객체 감지를 위해 YOLOv2 모델을 사용합니다. 이 애플리케이션은 신경망을 활용, 텐서플로우 모바일을 사용합니다. 안드로이드 기기에서 텐서플로우를 위한 YOLOv2 오픈소스 구현은 이번이 처음입니다. YOLOv2 모델에 대한 자세한 내용은 이 문서를 참조하십시오. [YOLO9000 Better, Faster, Stronger](https://arxiv.org/pdf/1612.08242.pdf).
**어플리케이션 컴파일과 실행 방법:**

필요조건:

* 설치 [Android Studio](https://developer.android.com/studio/index.html);
* Android 6.0 (API level 23) 이상을 어플리케이션 실행에 필요로 합니다. -> Camera2 API 라이브러리 활용;

원본 레포짓:

* `https://github.com/szaza/android-yolo-v2.git`;

어떻게 동작합니까??

![안드로이드 yolo v2 샘플 이미지](https://github.com/szaza/android-yolo-v2/blob/master/sample/android-yolo-v2.png)
![안드로이드 yolo v2 샘플 이미지](https://github.com/szaza/android-yolo-v2/blob/master/sample/android-yolo-v2.1.png)

보다 정확한 솔루션을 원한다면 서버 응용 프로그램을 생성하십시오. 여기에서 관련 프로젝트 보기:
* [텐서플로우 예시 Java API](https://sites.google.com/view/tensorflow-example-java-api/home)
* [텐서플로우 YOLOv2에 대한 Java 어플리케이션 서버 예시 모델](https://sites.google.com/view/tensorflow-example-java-api/tensorflow-java-api-with-spring-framework)
