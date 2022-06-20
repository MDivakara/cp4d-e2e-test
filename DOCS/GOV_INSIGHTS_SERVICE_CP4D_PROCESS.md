# ms-e2e-test-service 


 # Service Build Process
 1. Make changes in a `temp` branch and raise a PR with the changes against `develop` branch.
    
    For changes specific to a release, raise PR against respective branch like `release-client-name`
 
 2. Before merging PR to the develop branch, it needs to pass all the test cases.(unit, functional, integration).
 
 3. After successful PR build, at-least one reviewer needs to approved the PR.
 
 4. After Step 1 & 2 check passed, one can merge its PR.
 
 5. After merging PR, travis job will trigger a build process which includes :
 	- building of jars, war and docker image.
 	- running of unit, functional, integration tests.
 	- creating docker image.
 	- uploading/pushing docker image to docker repo.
 	- upload/push all jars/wars to repo.
 
 5. Once build passed, new build is available for consumption.
# Manual Deployment of service

1. Build the jars and war file
```
./gradlew clean  
./gradlew build -x test
```



