import numpy as np
import cv2
from PIL import Image
import base64
import io

def msg_to_bin(msg):
    if type(msg) == str:
        return ''.join([format(ord(i), "08b") for i in msg])
    elif type(msg) == bytes or type(msg) == np.ndarray:
        return [format(i, "08b") for i in msg]
    elif type(msg) == int or type(msg) == np.uint8:
        return format(msg, "08b")
    else:
        raise TypeError("Input type not supported")

def show_data(img):
    bin_data = ""
    for values in img:
        for pixels in values:
            # converting the Red, Green, Blue values into binary format
            r, g, b = msg_to_bin(pixels)
            # data extraction from the LSB of Red pixel
            bin_data += r[-1]
            # data extraction from the LSB of Green pixel
            bin_data += g[-1]
            # data extraction from the LSB of Blue pixel
            bin_data += b[-1]
    # splitting by 8-bits
    allBytes = [bin_data[i: i + 8] for i in range(0, len(bin_data), 8)]
    # converting from bits to characters
    decodedData = ""
    for bytes in allBytes:
        decodedData += chr(int(bytes, 2))
        # checking if we have reached the delimiter which is "#####"
        if decodedData[-5:] == "#####":
            break
    # print(decodedData)
    # removing the delimiter to display the actual hidden message
    return decodedData[:-5]

def decodeText(img):
    #resizedImg = cv2.resize(img, (500, 500))
    text = show_data(img)
    return text


def main(image):
    im_bytes = base64.b64decode(image)
    im_arr = np.frombuffer(im_bytes, dtype=np.uint8)  # im_arr is one-dim Numpy array
    img = cv2.imdecode(im_arr, flags=cv2.IMREAD_COLOR)

    text = decodeText(img)

    return text

"""
img = cv2.imread('eee.png')
_, im_arr = cv2.imencode('.png', img)  # im_arr: image in Numpy one-dim array format.
im_bytes = im_arr.tobytes()
im_b64 = base64.b64encode(im_bytes)

im_bytes = base64.b64decode(im_b64)
im_arr = np.frombuffer(im_bytes, dtype=np.uint8)  # im_arr is one-dim Numpy array
img = cv2.imdecode(im_arr, flags=cv2.IMREAD_COLOR)
"""

'''
text =decodeText(img)
print(text)

cv2.imshow("dec",img)
cv2.waitKey(0)'''

'''an_image = Image.open("eee.png")
output = io.BytesIO()
an_image.save(output, format="png")
image_as_string = output.getvalue()'''

#text =decodeText(ii)
#print(text)
"""
img = cv2.imread('eee.png')
_, im_arr = cv2.imencode('.png', img)  # im_arr: image in Numpy one-dim array format.
im_bytes = im_arr.tobytes()
im_b64 = base64.b64encode(im_bytes)

print(main(str(im_b64,'utf-8')))"""



#######################################################################################33