from loss_and_model import *
import gc
import base64
CONTENT_WEIGHT = 1
STYLE_WEIGHT = 1000


e = 0


def transfer(content, style):
    global e
    style_image = imageProcess(style)
    content_image = imageProcess(content)
    # target_image = content_image.clone()
    target_image = torch.randn([1, 3, 512, 512]).data.clamp_(0, 1).to(DEVICE)

    self_model, content_losses, style_losses = model_generate(style_image, content_image)
    opt = torch.optim.LBFGS([target_image.requires_grad_(True)])
    while e < 80:
        def closure():
            global e
            target_image.data.clamp_(0, 1)  # 消除杂点
            L_content = 0
            L_style = 0
            opt.zero_grad()
            self_model(target_image)

            for i in content_losses:
                L_content += i.loss

            for i in range(5):
                if i < 3:
                    L_style += style_losses[i].loss * 0.1
                else:
                    L_style += style_losses[i].loss * 3

            L = CONTENT_WEIGHT * L_content + STYLE_WEIGHT * L_style
            L.backward()

            e += 1
            if e % 10 == 0:
                print(e)
            return L

        opt.step(closure)
        target_image.data.clamp_(0, 1)
    a = base64.b64encode(bytes(target_image.cpu().detach().numpy()))
    a = str(a)
    output = show(target_image.cpu().squeeze(0))
    output.save("3.jpg")
    del self_model, content_losses, style_losses, content_image, style_image
    with torch.no_grad():
        torch.cuda.empty_cache()
    gc.collect()
    return a


if __name__ == "__main__":
    content_file = "content/" + str(1) + ".jpg"
    style_file = "style/" + str(1) + ".jpg"
    res = transfer(content_file, style_file)
