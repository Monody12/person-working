# 个人网盘
在该项目中，用户可以将私人文件上传到网盘中，随时使用浏览器预览、下载文件。
用户还可以通过url将文件分享给其他用户、访客。

## 部署到服务器

### 一、安装如下依赖

```shell

# 1. 安装docker
# 安装依赖包
yum install -y yum-utils device-mapper-persistent-data lvm2
yum -y install docker-ce

# 2. 安装mysql

docker run -p 3306:3306 --name mysql \
-e MYSQL_ROOT_PASSWORD=12345678 \
-d mysql:8.0.27

# 3. 安装redis
docker run -d --name redis -p 6379:6379 --memory=100m redis:6.2.6

# 4. 安装mongodb
docker run -d --name mongo -p 27017:27017 -e MONGO_INITDB_ROOT_USERNAME=root -e MONGO_INITDB_ROOT_PASSWORD=1234 mongo:4.4.6

# 5. 安装rabbitmq
docker run -d --name rabbitmq \
    --publish 5672:5672 \
    --publish 15672:15672 \
    --hostname rabbitmq \
    --env RABBITMQ_DEFAULT_USER=guest \
    --env RABBITMQ_DEFAULT_PASS=guest \
    --memory 2g \
    rabbitmq:3.8-management
    
# 6. 安装nacos
docker run -d --name nacos -p 8848:8848 -e MODE=standalone nacos/nacos-server:1.4.1

```

### 二、初始化数据库

创建 person_working_netdisk 数据库，执行 netdisk/src/main/resources/person_working_netdisk.sql

创建 person_working_user 数据库，执行 user-service/src/main/resources/person_working_user.sql

### 三、修改配置文件

1. 修改 redis 配置

    前往 redis-api/src/main/resources/redis.properties 文件中修改
    修改项：redis ip、端口、密码
2. 修改 nacos与网关 配置

    前往 gateway/src/main/resources/bootstrap.yaml 文件中修改
    修改项：网关端口nacos地址、允许跨域地址
3. 修改 netdisk-service 配置

    前往 netdisk/src/main/resources/bootstrap.yaml 中指定nacos位置、启用的配置文件（以dev示例）
       application-dev.yml
    修改项：mongodb配置、rabbitmq配置、mysql配置、文件存放路径、文件大小限制
4. 修改 user-service 配置

    前往 user-service/src/main/resources/bootstrap.yaml 中指定nacos位置、启用的配置文件（以dev示例）
       application-dev.yml
    修改项：mysql配置、邮件服务配置、用户默认头像url


### 四、部署服务

#### 使用jar包部署

1. 打jar包

   编译环境 JDK 1.8，Maven 3.6
        
   已知问题：在JDK17环境下，运行netdisk-1.0-SNAPSHOT.jar会导致移动文件功能报错，使用JDK1.8则不会。

   在父项目中 mvn clean package
   
   将以下jar包拷贝到服务器，并依次运行（java -jar）
   
   1. gateway/target/gateway-1.0-SNAPSHOT.jar
   2. user-service/target/user-service-1.0-SNAPSHOT.jar
   3. netdisk/target/netdisk-1.0-SNAPSHOT.jar

2. 修改hosts（可选）

若使用默认的配置的ip：vm.local，则需要修改hosts文件，将vm.local指向服务器ip


#### 使用docker部署

1. maven编译项目打jar包

2. 对三个模块进行docker镜像构建
   
   例如命令： docker build -t netdisk-gateway gateway/

   1. gateway
   2. user-service
   3. netdisk-service

3. 运行容器

   编写run或compose文件请

   1. 添加vm.local的hosts映射
   2. 注意放行端口
   3. 配置数据卷或挂载文件夹，避免数据丢失
   
#### 使用Jenkins部署

pipeline script 示例

```groovy

pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/Monody12/person-working.git'
            }
        }
        
        stage('Maven Build') {
            steps {
                withMaven(maven: 'Maven') {
                    sh 'mvn clean package -DskipTests'
                }
            }
        }
        
        stage('Build Docker Images') {
            steps {
                script {
                    sh 'docker build -t netdisk-gateway gateway/'
                    sh 'docker build -t netdisk-user-service user-service/'
                    sh 'docker build -t netdisk-main netdisk/'
                }
            }
        }
        
        stage('Deploy') {
            steps {
                script {
                    // 停止并删除已有的容器（如果存在）
                    sh 'docker stop gateway || true'
                    sh 'docker stop user-service || true'
                    sh 'docker stop netdisk || true'
                    sh 'docker rm gateway || true'
                    sh 'docker rm user-service || true'
                    sh 'docker rm netdisk || true'
                    
                    // 删除旧的镜像
                    sh 'docker image prune -f'

                    // 运行新的容器
                    sh 'docker run -d --name gateway -p 8080:8080 --add-host=vm.local:192.168.0.89 netdisk-gateway'
                    sh 'docker run -d --name user-service -p 8001:8001 --add-host=vm.local:192.168.0.89 -v /root/netdisk/upload:/Users/monody/Desktop/upload netdisk-user-service'
                    sh 'docker run -d --name netdisk -p 8002:8002 --add-host=vm.local:192.168.0.89 netdisk-main'
                }
            }
        }
    }
}


```

### 五、访问服务网盘项目

开发环境使用以下地址，生产环境请使用服务器ip和自定的端口

浏览器访问 http://localhost:8080/netdisk/index.html
