from flask import Flask, request
import base64
import time
from PIL import Image

app = Flask(__name__)
a = 0
b = 0


@app.route('/', methods=['post', 'get'])
def title():
    # print(data)
    return "Hello boy"


@app.route('/get', methods=['get', 'post'])  # 加了post，该接口才能接收发来的数据
def index():
    global a, b
    content = request.form.get('content')  # 获取由前端发过来的base64数据
    style = request.form.get('style')

    if content is not None:
        content = content.replace(' ', '+')  # java进行的base64编码不能直接转换，也可能是因为传输过程发生了变化，我也不太清楚，总之加上这句才行。
        a += 1
        contentfile = "./content/" + time.strftime('%Y%m%d_%H%M%S') + '.jpg'  # 文件的保存路径
        contentdata = base64.b64decode(content)  # 进行base64解码
        print(type(contentdata))  #
        a = Image.frombytes('RGB', (512, 512), contentdata)
        print(type(a))
        # file1 = open(contentfile, 'wb')  # 以二进制写方式打开文件
        # file1.write(contentdata)  # 写入文件
        # file1.close()  # 关闭文件
        # return contentfile  # 返回保存的文件路径

    if style is not None:
        style = style.replace(' ', '+')
        b += 1
        stylefile = "./style/" + time.strftime('%Y%m%d_%H%M%S') + '.jpg'  # 文件的保存路径
        styledata = base64.b64decode(style)  # 进行base64解码
        print(type(styledata))
        # file2 = open(stylefile, 'wb')  # 以二进制写方式打开文件
        # file2.write(styledata)  # 写入文件
        # file2.close()  # 关闭文件
        # return stylefile  # 返回保存的文件路径

    if a == b:
        pass

    return '-2'


if __name__ == '__main__':
    app.run(host='10.241.127.208', port=30000)
