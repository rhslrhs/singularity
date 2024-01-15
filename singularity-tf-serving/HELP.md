# docker container - tensorflow/serving

``` shell
docker run -d -p 8500:8500 -p 8501:8501 -e MODEL_NAME=numImage --name numImage tensorflow/serving
docker cp numImage/ numImage:/models/

```