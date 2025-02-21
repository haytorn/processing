
/**
 NyARToolkit for proce55ing/1.0.0
 (c)2008-2011 nyatla
 airmail(at)ebony.plala.or.jp
 
 NyIdマーカを利用する例です。0番のidマーカを用意して下さい。
 
 This sample is simple NyId sample.
 The marker id is #0.
 */
import processing.video.*;
import jp.nyatla.nyar4psg.*;

Capture cam;
MultiMarker nya;

void setup() {
  size(640, 480, P3D);
  colorMode(RGB, 100);
  println(MultiMarker.VERSION);
  cam=new Capture(this, 640, 480);
  nya=new MultiMarker(this, width, height, "../../data/camera_para.dat", NyAR4PsgConfig.CONFIG_PSG);
  nya.addNyIdMarker(0, 80);
  cam.start();
}

void draw()
{
  if (cam.available() !=true) {
    return;
  }
  cam.read();
  nya.detect(cam);
  background(0);
  nya.drawBackground(cam);//frustumを考慮した背景描画
  if ((!nya.isExist(0))) {
    return;
  }
  nya.beginTransform(0);
  fill(0,0,225);
  translate(0,0,20);
  box(40);println(nya.getNyId(0));
  fill(0);
  textSize(50);
  String s = "立方体";
  text( 0, 20, 20);
  println(nya.getNyId(0));
  nya.endTransform();
}