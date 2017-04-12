package com.ov.omniwificam;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;
import android.util.Log;


import android.content.Context;
import android.opengl.GLSurfaceView;

import android.opengl.GLU;
import android.opengl.GLUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
//包名必须是这个,jni
public class Vout implements GLSurfaceView.Renderer{
	
    private static final String TAG = "ov780wifi";
    private Orientation mOrientation = Orientation.HORIZONTAL;
    
	/* Video orientation parameters */
    public enum Orientation {
        HORIZONTAL,
        VERTICAL
    }

    public Vout(Context context) {
        mContext = context;
        mQuad = new Quad();
	}
	/*
	 *FIXME: we need a method to import JNI API to Vout. 
	 *       we can also set mOVJNI in construct func, or set the nativeAPI as static in OVWIFICamJNI.java 
	 *		 which is better?
	 */
	public void setJNI(OVWIFICamJNI ovJNI){
		mOVJNI = ovJNI;
	}
    
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        /*
         * By default, OpenGL enables features that improve quality
         * but reduce performance. One might want to tweak that
         * especially on software renderer.
         */
        gl.glDisable(GL10.GL_DITHER);

        /*
         * Some one-time OpenGL initialization can be made here
         * probably based on features of this particular context
         */
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT,GL10.GL_FASTEST);
        gl.glClearColor(0.25f, 0.25f, 0.25f, 1);
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glEnable(GL10.GL_TEXTURE_2D);

        /*
         * Create our texture. This has to be done each time the
         * surface is created.
         */

        int[] textures = new int[1];
        gl.glGenTextures(1, textures, 0);

        mTextureID = textures[0];
        gl.glBindTexture(GL10.GL_TEXTURE_2D, mTextureID);

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_NEAREST);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);

        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
        gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);

        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE, GL10.GL_REPLACE);
        texWidth=1;
        texHeight=1;
        byte[] texData = new byte[texWidth * texWidth * 2];
        
        //texData[0]=(byte)0xc7;
        //texData[1]=(byte)0x39;
        texData[0]=(byte)0x08;
        texData[1]=(byte)0x42;
        /* texData[0]=(byte)0x0;
        texData[1]=(byte)0xf1;
       
       	texData[2]=(byte)0x0;
        texData[3]=(byte)0x4;
        
        texData[4]=(byte)0x1f;
        texData[5]=(byte)0x0;
        
        texData[6]=(byte)0xef;
        texData[7]=(byte)0x7b;
     
        texData[8]=(byte)0x0;
        texData[9]=(byte)0xf1;
       
        texData[10]=(byte)0x0;
        texData[11]=(byte)0x4;
        
        texData[12]=(byte)0x1f;
        texData[13]=(byte)0x0;
        
        texData[14]=(byte)0xef;
        texData[15]=(byte)0x7b;*/
        
        ByteBuffer byteBufferInit = ByteBuffer.wrap(texData);

        gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGB, texWidth,
        		texHeight, 0, GL10.GL_RGB, GL10.GL_UNSIGNED_SHORT_5_6_5,
        		byteBufferInit);
		
			
/*
        InputStream is = mContext.getResources()
                .openRawResource(R.raw.cone);
        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
            } catch(IOException e) {
                // Ignore.
            }
        }

        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
        bitmap.recycle();
		*/
        //add by kfir
        if(mOrientation == Orientation.VERTICAL){
        		gl.glRotatef(90F, 0F, 0F, 1F);
        }
    }

    public void onDrawFrame(GL10 gl) {
        gl.glTexEnvx(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,
                GL10.GL_MODULATE);

        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);
        
        if (mustInit)
        {
        	texWidth = getAlignedSize(frameWidth);
        	texHeight = getAlignedSize(frameHeight);

        	byte[] texData = new byte[texWidth * texHeight * 2];
	        ByteBuffer byteBufferInit = ByteBuffer.wrap(texData);
	
	        gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGB, texWidth,
	        		texHeight, 0, GL10.GL_RGB, GL10.GL_UNSIGNED_SHORT_5_6_5,
	        		byteBufferInit);
	        
	        mQuad.computeTexCoord(texWidth, texHeight, frameWidth, frameHeight);
	        onSurfaceChanged(gl, surfaceWidth, surfaceHeight); // Compute AspectRatio

	        /* Wrap the image buffer to the byte buffer. */
	        byteBuffer = ByteBuffer.wrap(image);

	        mustInit = false;
        }

		if(chgdispmode){
			onSurfaceChanged(gl, surfaceWidth, surfaceHeight);
			chgdispmode=false;
		}

		if(IsPan){
			onSurfaceChanged(gl, surfaceWidth, surfaceHeight);
			IsPan=false;
		}
        
        if (hasReceivedFrame)
        {
	        gl.glTexSubImage2D(GL10.GL_TEXTURE_2D, 0, 0, 0, frameWidth,
	        		frameHeight, GL10.GL_RGB, GL10.GL_UNSIGNED_SHORT_5_6_5,
	        		byteBuffer);

        }
        mQuad.draw(gl);

//       	hasReceivedFrame = false;
    }

    public void onSurfaceChanged(GL10 gl, int w, int h) {
		Log.d(TAG, String.format("Surface: %d, %d", w, h) );
        gl.glViewport(0, 0, w, h);

        /*
         * Retain surface size, to be able to correct the AspectRatio
         * on demand when a new video starts
         */
        surfaceWidth = w;
        surfaceHeight = h;

        /*
        * Set our projection matrix. This doesn't have to be done
        * each time we draw, but usually a new projection needs to
        * be set when the viewport is resized.
        */

        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();

        /*
         * glFrustumf expand the clipping volume vertically or horizontally : it adds bars.
         * how big those bars should be depends on both the video ratio and the surface ratio
         */
        float vRatio, hRatio;
        if(frameWidth*frameHeight>0){
        	vRatio=(float)surfaceHeight/frameHeight;
        	hRatio=(float)surfaceWidth/frameWidth;
        	bestRatio=vRatio;
        	
        }
        else {
        vRatio = 1;
        hRatio = (float)surfaceWidth/(float)surfaceHeight;
        bestRatio=vRatio;
        }
        //aRatio = hRatio/vRatio;
		float old_zoomedscale = zoomedscale;
		Log.d(TAG, String.format("dispmode: %d", dispmode) );
		 if(dispmode==0){
		   	if(hRatio>vRatio)
        		zoomedscale=vRatio;
        	else
        		zoomedscale=hRatio;
        	gl.glFrustumf(-hRatio/zoomedscale, hRatio/zoomedscale, -vRatio/zoomedscale, vRatio/zoomedscale, 3f, 7f);
			myoffset = lastyoff = 0;
			if( IsTriVga ){
				lastxoff *= zoomedscale/old_zoomedscale;
			}else{
				mxoffset = lastxoff = 0;
			}
        }
        else if(dispmode==1){
        	zoomedscale=hRatio;
        	gl.glFrustumf(-1f, 1f,- 1f, 1f, 3f, 7f);
			myoffset = lastyoff = 0;
			if( IsTriVga ){
				lastxoff *= zoomedscale/old_zoomedscale;
        	}else{
				mxoffset = lastxoff = 0;
			}
		}
        else if(dispmode==2){
        	zoomedscale=1;
        	gl.glFrustumf(-hRatio/zoomedscale, hRatio/zoomedscale, -vRatio/zoomedscale, vRatio/zoomedscale, 3f, 7f);
			myoffset = lastyoff = 0;
			if( IsTriVga ){
				lastxoff *= zoomedscale/old_zoomedscale;
			}else{
				mxoffset = lastxoff = 0;
			}
         }
        else if(dispmode==3){
        	if(zoomscale*zoomedscale>vRatio){
        		boolean OutofRange=false;
        		lastyoff=(OutofRange=-vRatio/zoomscale/zoomedscale+(myoffset+lastyoff)/surfaceHeight<-1f)?
        				(-1f+vRatio/zoomscale/zoomedscale)*surfaceHeight:lastyoff;
        		myoffset=OutofRange?0:myoffset;
        		lastyoff=(OutofRange= vRatio/zoomscale/zoomedscale+(myoffset+lastyoff)/surfaceHeight> 1f)?
        				(  1f-vRatio/zoomscale/zoomedscale)*surfaceHeight:lastyoff;
        		myoffset=OutofRange?0:myoffset;        		
        	}
        	else
        		myoffset=lastyoff=0;
			
			if( !IsTriVga ){
        		if(zoomscale*zoomedscale>hRatio){
        			boolean OutofRange=false;
        			lastxoff=(OutofRange=-hRatio/zoomscale/zoomedscale+(mxoffset+lastxoff)/surfaceWidth<-1f)?
        					(-1f+hRatio/zoomscale/zoomedscale)*surfaceWidth:lastxoff;
        			mxoffset=OutofRange?0:mxoffset;
        			lastxoff=(OutofRange=hRatio/zoomscale/zoomedscale+(mxoffset+lastxoff)/surfaceWidth>1f)?
        					(1f-hRatio/zoomscale/zoomedscale)*surfaceWidth:lastxoff;
        			mxoffset=OutofRange?0:mxoffset;        		
        		}
        		else
        			mxoffset=lastxoff=0;
        	
				gl.glFrustumf(-hRatio/zoomscale/zoomedscale-(mxoffset+lastxoff)/surfaceWidth,
        					hRatio/zoomscale/zoomedscale-(mxoffset+lastxoff)/surfaceWidth, 
        					-vRatio/zoomscale/zoomedscale+(myoffset+lastyoff)/surfaceHeight, 
        					vRatio/zoomscale/zoomedscale+(myoffset+lastyoff)/surfaceHeight, 
        					3f, 7f);
			}else{
				gl.glFrustumf(-hRatio/zoomscale/zoomedscale,
        					hRatio/zoomscale/zoomedscale, 
        					-vRatio/zoomscale/zoomedscale+(myoffset+lastyoff)/surfaceHeight, 
        					vRatio/zoomscale/zoomedscale+(myoffset+lastyoff)/surfaceHeight, 
        					3f, 7f);
			}

        }
		
		GLU.gluLookAt(gl, 0, 0, 2.001f, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

		if( IsTriVga ){
			zoomscale = (zoomscale != 0)? zoomscale : 1;	
			float zoom = zoomscale * zoomedscale;
			if( mxoffset == 0) return;
			int h_shift = (int)( (mxoffset + lastxoff) / zoom );
			h_shift = (frameWidth != 0)? (h_shift % frameWidth):h_shift;
			h_shift = (h_shift < 0)? (h_shift + frameWidth) : h_shift;
        	
			//Log.d(TAG, "[h_shift]: lastxoff= " + lastxoff + "  mxoffset= " + mxoffset + " zoom= " + zoom + " h_shift= " + h_shift);
			if(mOVJNI != null){
				mOVJNI.nativeSetPicShift(h_shift, 0);// set device idx to 0. for test temporarily only.
			}
		}
    }
    
    private int getAlignedSize(int i_size)
    {
        /* Return the nearest power of 2 */
        int i_result = 1;
        while(i_result < i_size)
            i_result *= 2;

        return i_result;
    }

    /* Manually change video orientation */
    public void setOrientation (Orientation orient) {
    		this.mOrientation = orient;
    }


    private Context mContext;
    private Quad mQuad;
    private int mTextureID;
	private OVWIFICamJNI mOVJNI = null;

    //public boolean mustInit = false;
    public boolean mustInit = false;
    public boolean hasReceivedFrame = false;

	public boolean chgdispmode=false;
	public int dispmode=0;

    public int surfaceWidth;
    public int surfaceHeight;

    public int frameWidth;
    public int frameHeight;

    private int texWidth;
    private int texHeight;

    public byte[] image;
    private ByteBuffer byteBuffer;
	public float zoomscale, zoomedscale=1, bestRatio;
	public float mxoffset=0;
	public float myoffset=0;
	public boolean IsPan;
	public float lastxoff=0;
	public float lastyoff=0;
	public boolean IsTriVga = false;
}

class Quad {
    public Quad() {
    	computeTexCoord(1, 1, 1, 1);
    }
    
    public void computeTexCoord(int texWidth, int texHeight,
    		int frameWidth, int frameHeight) {

        // Buffers to be passed to gl*Pointer() functions
        // must be direct, i.e., they must be placed on the
        // native heap where the garbage collector cannot
        // move them.
        //
        // Buffers with multi-byte datatypes (e.g., short, int, float)
        // must have their byte order set to native order

        ByteBuffer vbb = ByteBuffer.allocateDirect(VERTS * 3 * 4);
        vbb.order(ByteOrder.nativeOrder());
        mFVertexBuffer = vbb.asFloatBuffer();

        ByteBuffer tbb = ByteBuffer.allocateDirect(VERTS * 2 * 4);
        tbb.order(ByteOrder.nativeOrder());
        mTexBuffer = tbb.asFloatBuffer();

        ByteBuffer ibb = ByteBuffer.allocateDirect(VERTS * 2);
        ibb.order(ByteOrder.nativeOrder());
        mIndexBuffer = ibb.asShortBuffer();

        float[] coords = {
                // X, Y, Z
        		-1f, -1f, -1f,
                1f, -1f, -1f,
                -1f, 1f, -1f,
                1f, 1f, -1f
        };
        
        float f_width = (float)frameWidth / texWidth;
        float f_height = (float)frameHeight / texHeight;
        
        float[] tex_coords = {
                // X, Y, Z
        		0f,      f_height, -1f,
        		f_width, f_height, -1f,
                0f,      0f,       -1f,
                f_width, 0f,       -1f
        };

        for (int i = 0; i < VERTS; i++) {
            for(int j = 0; j < 3; j++) {
                mFVertexBuffer.put(coords[i*3+j]);
            }
        }

        for (int i = 0; i < VERTS; i++) {
            for(int j = 0; j < 2; j++) {
                mTexBuffer.put(tex_coords[i*3+j]);
            }
        }

        for(int i = 0; i < VERTS; i++) {
            mIndexBuffer.put((short) i);
        }

        mFVertexBuffer.position(0);
        mTexBuffer.position(0);
        mIndexBuffer.position(0);
    }

    public void draw(GL10 gl) {
        gl.glFrontFace(GL10.GL_CCW);
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, mFVertexBuffer);
        gl.glEnable(GL10.GL_TEXTURE_2D);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, mTexBuffer);
        gl.glDrawElements(GL10.GL_TRIANGLE_STRIP, VERTS,
                GL10.GL_UNSIGNED_SHORT, mIndexBuffer);
    }

    private final static int VERTS = 4;

    private FloatBuffer mFVertexBuffer;
    private FloatBuffer mTexBuffer;
    private ShortBuffer mIndexBuffer;
}
