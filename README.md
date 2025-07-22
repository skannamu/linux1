**극극극극축소판 헝거게임**

Maven기반 빌드
빌드방법 : 
1. cd ~/"TOPDIR" <-프로젝트 최상위디렉토리로 이동(clone해온 위치)
2. mvn clean package <- ~/TOPDIR/target/original-tagbattle-1.0.jar생성
2.1. linux서버구동시 :
    ~# cp ~/TOPDIR/target/original-tagbattle-1.0.jar ~/paper서버디렉토리/plugins/ && cd ~/paper서버디렉토리
    ~# java -Xms1G -Xmx2G -jar paper.jar <- 구동
2.2. 서버주소 :
    본인 컴퓨터 공인ip:25565 (linux시 포트포워딩 필수, window시 방화벽 인바운딩설정 필요(25565포트)
    +공유기 사용시 공유기 포트포워딩도 필수(25565포트)
