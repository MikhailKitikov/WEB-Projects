FROM python:3.8
RUN mkdir /smeliana
COPY . /smeliana
WORKDIR /smeliana
ENV PYTHONPATH=${PYTHONPATH}:${PWD}

RUN pip3 install poetry
RUN pip3 install torch torchvision torchaudio --index-url https://download.pytorch.org/whl/cu118
RUN poetry config virtualenvs.create false
RUN poetry install

ENTRYPOINT ["python", "./main.py"]
