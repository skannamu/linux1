# **극극극극축소판 헝거게임**

Maven기반 빌드  
빌드방법 :

1. cd ~/"TOPDIR" ← 프로젝트 최상위 디렉토리로 이동 (clone해온 위치)  
2. mvn clean package ← ~/TOPDIR/target/original-tagbattle-1.0.jar 생성  
2.1. linux 서버 구동 시 :<br>
    ~# cp ~/TOPDIR/target/original-tagbattle-1.0.jar ~/paper서버디렉토리/plugins/ && cd ~/paper서버디렉토리
   <br>
    ~# java -Xms1G -Xmx2G -jar paper.jar ← 구동  

2.2. 서버 주소 :<br>
    본인 컴퓨터 공인 IP:25565  
    (linux 시 포트포워딩 필수,  
    windows 시 방화벽 인바운드 규칙 설정 필요 (포트 25565))  
    + 공유기 사용 시 공유기 포트포워딩도 필수 (25565 포트)
