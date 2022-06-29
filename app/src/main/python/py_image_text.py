import numpy as np
import cv2
from PIL import Image
import base64
import io

def main(image,data):
    im_bytes = base64.b64decode(image)
    im_arr = np.frombuffer(im_bytes, dtype=np.uint8)  # im_arr is one-dim Numpy array
    img = cv2.imdecode(im_arr, flags=cv2.IMREAD_COLOR)

    finalimage = hide_data(img, data)

    _, im_arr = cv2.imencode('.png', finalimage)  # im_arr: image in Numpy one-dim array format.
    im_bytes = im_arr.tobytes()
    im_b64 = base64.b64encode(im_bytes)
    return ""+str(im_b64,'utf-8')


def msg_to_bin(msg):
    if type(msg) == str:
        return ''.join([format(ord(i), "08b") for i in msg])
    elif type(msg) == bytes or type(msg) == np.ndarray:
        return [format(i, "08b") for i in msg]
    elif type(msg) == int or type(msg) == np.uint8:
        return format(msg, "08b")
    else:
        raise TypeError("Input type not supported")


def hide_data(img, secret_msg):
    nBytes = img.shape[0] * img.shape[1] * 3 // 8
    # checking whether the number of bytes for encoding is less
    # than the maximum bytes in the image
    if len(secret_msg) > nBytes:
        raise ValueError("Error encountered insufficient bytes, need bigger image or less data!!")
    secret_msg += '#####'  # we can utilize any string as the delimiter
    dataIndex = 0
    # converting the input data to binary format using the msg_to_bin() function
    bin_secret_msg = msg_to_bin(secret_msg)

    # finding the length of data that requires to be hidden
    dataLen = len(bin_secret_msg)
    for values in img:
        for pixels in values:
            # converting RGB values to binary format
            r, g, b = msg_to_bin(pixels)
            # modifying the LSB only if there is data remaining to store
            if dataIndex < dataLen:
                # hiding the data into LSB of Red pixel
                pixels[0] = int(r[:-1] + bin_secret_msg[dataIndex], 2)
                dataIndex += 1
            if dataIndex < dataLen:
                # hiding the data into LSB of Green pixel
                pixels[1] = int(g[:-1] + bin_secret_msg[dataIndex], 2)
                dataIndex += 1
            if dataIndex < dataLen:
                # hiding the data into LSB of Blue pixel
                pixels[2] = int(b[:-1] + bin_secret_msg[dataIndex], 2)
                dataIndex += 1
                # if data is encoded, break out the loop
            if dataIndex >= dataLen:
                break

    return img


'''ii =cv2.imread("Atomhawk_Marvel_Guardians-of-the-Galaxy_Concept-Art_Spacecraft-Design_Quills-Ship2.jpg")
print(ii.shape)

pil_im=Image.fromarray(ii)
buff = io.BytesIO()
pil_im.save(buff,format="PNG")
img_str = base64.b64encode(buff.getvalue())

im =main(str(img_str,'utf-8'),"hi here")

decoded_data =base64.b64decode(im)
np_data = np.fromstring(decoded_data,np.uint8)
img =cv2.imdecode(np_data,cv2.IMREAD_UNCHANGED)

cv2.imshow("dec",img)
cv2.waitKey(0)
'''