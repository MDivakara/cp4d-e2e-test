## About

https://github.com/shaiknawazz1/cp4d-e2e-test.git

## Setting up minimal development environment

*Required Tools*
   * Git 1.7.1+
   * Java 1.7+
   * Docker 1.12+
   * Docker Compose 1.12+

1. Clone this Git repository, best using SSH.
3. With Gradle, set up WebSphere Liberty Profile runtime:
   `./gradlew setupLiberty`. Once completed, you can find your Liberty
   installation in `build/wlp` directory.
4. Build and deploy the application with `./gradlew deploy`.
5. Start Liberty server with `./gradlew libertyStart`. After a few seconds, the
   application will be available at http://localhost:9080/
6. Use any REST client or visit http://localhost:9080/api/explorer to work with
   the Open API (Swagger) console.
7. To stop the Liberty server, use `./gradlew libertyStop`.

## Configuring the development environment 
1. Recreate the Liberty server, redeploy the application and start the server
   with:
   ```
   ./gradlew clean
   ./gradlew build -x test
   ./gradlew setupLiberty
   ./gradlew deploy
   ./gradlew libertyStop
   ./gradlew libertyStart
   ```
## Building docker image locally and deployed to UG machine
Docker image build is orchestrated with Gradle scripting.

1. Login to docker using credentials
   ```
   docker login docker-repo-url
   ```
2. Run the following command to build the Docker image:
   ```
   ./gradlew buildDockerImage
   ```
3. Save docker image in tar file
   ```
   docker save -o <path for generated tar file> <image_id>
   ```
4. load docker image from tar file
   ```
   docker load -i <path to image tar file>
   ```
## Thank You Very much !!!! !!
