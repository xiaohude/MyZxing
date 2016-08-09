package com.smarttiger.myzxing;

import java.util.EnumMap;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BuildView extends Activity implements OnClickListener {

    private static final int BLACK = 0xFF000000;
    private static final int WHITE = 0xFF000000;
    
	private TextView scanningText;
	private TextView buildText;
	private EditText editText;
	private ImageView imageView;
	private String text = "123456";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.buildview);
		
		scanningText = (TextView) findViewById(R.id.scanning_text);
		buildText = (TextView) findViewById(R.id.build_text);
		scanningText.setOnClickListener(this);
		buildText.setOnClickListener(this);
		editText = (EditText) findViewById(R.id.edit_text);
		imageView = (ImageView) findViewById(R.id.image_view);
		
		buildQrCode();
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		int id = view.getId();
		if(id == R.id.scanning_text) {
			Intent intent = new Intent(this, ScanningView.class);
			startActivity(intent);
		} else if (id == R.id.build_text) {
			text = editText.getText().toString();
			buildQrCode();
		}
	}
	
	
	private void buildQrCode () {
		try {
			Bitmap mQrBitmap = encodeAsBitmap();
			if(mQrBitmap != null){
				imageView.setImageBitmap(mQrBitmap);
			}
		} catch (WriterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private Bitmap encodeAsBitmap() throws WriterException {
        if (TextUtils.isEmpty(text)) {
        	Toast.makeText(this, "字符串为空！", 0).show();
            return null;
        }
        BarcodeFormat format = BarcodeFormat.valueOf(BarcodeFormat.QR_CODE.toString());
        Map<EncodeHintType, Object> hints = null;
        String encoding = guessAppropriateEncoding(text);
        if (encoding != null) {
            hints = new EnumMap<EncodeHintType, Object>(EncodeHintType.class);
            hints.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        int mScaleImageHeight = 480;
        BitMatrix result;
        try {
        	System.out.println("text========="+text);
            result = new MultiFormatWriter().encode(text, format, mScaleImageHeight,
            		mScaleImageHeight,hints);
        	System.out.println("result========="+result);
        } catch (IllegalArgumentException iae) {
            return null;
        }
        
        BitMatrix resMatrix = result;
        int width = resMatrix.getWidth();
        int height = resMatrix.getHeight();
        int[] pixels = new int[width * height];
        for(int y = 0;y<height;y++){
			for(int x = 0;x<width;x++)
			{
				if(resMatrix.get(x, y))
					pixels[y * width + x] = BLACK;
//				else
//					pixels[y * width + x] = WHITE;
			}
		}

        
        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        Matrix matrix = new Matrix();  
        matrix.postScale(mScaleImageHeight/width,mScaleImageHeight/height); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap,0,0,width,height,matrix,true); 
        return resizeBmp;
    }
	
	//获取当前字符串编码格式是否为UTF-8格式的。
	private static String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }
}
