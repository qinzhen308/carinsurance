package com.paulz.carinsurance.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 图形类
 * 
 * @author jjj
 * 
 * @date 2015-8-5
 */
public class ImageUtil {
	public static String filePath = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + "/baoxian/images/";// 头像存储路径;


	public static Uri temp_img_crop_uri;
	public static String temp_img_crop_path;


	static {
		File pathFile = new File(filePath);
		if (!pathFile.exists()) {
			pathFile.mkdirs();
		}
		temp_img_crop_path=filePath+"temp_img_crop.png";
		temp_img_crop_uri=Uri.fromFile(new File(temp_img_crop_path));
	}


	/**
	 * 获取相对路径
	 * 
	 * @param path
	 * @param name
	 * @return
	 */
	public static String bitmap2File(String path, String name) {

		Bitmap bitmap = compressImage(path);
		// Bitmap bitmap = getimage_(path);

		if (null == bitmap) {
			return null;
		}
		File file = null;
		try {
			File pathFile = new File(path.substring(0,path.lastIndexOf("/")));
			if (!pathFile.exists()) {
				pathFile.mkdirs();
			}
			file = new File(pathFile, name);
			if (file.exists()) {
				file.delete();
			}

			file.createNewFile();

			FileOutputStream fileOut = new FileOutputStream(file);
			// int size = 100;
			// if (bitmap.getHeight() > 1000 || bitmap.getWidth() > 1000) {
			// size = 80;
			// }
			bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOut);

			fileOut.flush();
			fileOut.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return file.getAbsolutePath();
	}



	// 按比例压缩
	// private static Bitmap getimage_(String srcPath) {
	// BitmapFactory.Options newOpts = new BitmapFactory.Options();
	// // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
	// newOpts.inJustDecodeBounds = true;
	// Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空
	//
	// newOpts.inJustDecodeBounds = false;
	// int w = newOpts.outWidth;
	// int h = newOpts.outHeight;
	// // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
	// float hh = 800f;// 这里设置高度为800f
	// float ww = 480f;// 这里设置宽度为480f
	// // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
	// int be = 1;// be=1表示不缩放
	// if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
	// be = (int) (newOpts.outWidth / ww);
	// } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
	// be = (int) (newOpts.outHeight / hh);
	// }
	// if (be <= 0)
	// be = 1;
	// newOpts.inSampleSize = be;// 设置缩放比例
	// // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
	// bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
	// return compressImage_(bitmap);// 压缩好比例大小后再进行质量压缩
	// }

	// 按质量压缩
	// private static Bitmap compressImage_(Bitmap image) {
	//
	// ByteArrayOutputStream baos = new ByteArrayOutputStream();
	// image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//
	// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
	// int options = 100;
	// while (baos.toByteArray().length / 1024 > 100) { //
	// 循环判断如果压缩后图片是否大于100kb,大于继续压缩
	// baos.reset();// 重置baos即清空baos
	// image.compress(Bitmap.CompressFormat.JPEG, options, baos);//
	// 这里压缩options%，把压缩后的数据存放到baos中
	// options -= 10;// 每次都减少10
	// }
	// ByteArrayInputStream isBm = new
	// ByteArrayInputStream(baos.toByteArray());//
	// 把压缩后的数据baos存放到ByteArrayInputStream中
	// Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//
	// 把ByteArrayInputStream数据生成图片
	// return bitmap;
	// }

	/**
	 * 读取图片属性：旋转的角度
	 * 
	 * @param path
	 *            图片绝对路径
	 * @return degree旋转的角度
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/**
	 * 旋转图片为正方向
	 * 
	 * @param angle
	 * @param bitmap
	 * @return Bitmap
	 */
	public static Bitmap rotaingImageView(int angle, Bitmap bitmap) {
		// 旋转图片 动作
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		// 创建新的图片
		Bitmap resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
				bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizedBitmap;
	}

	/**
	 * 计算图片的缩放值
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			final int heightRatio = Math.round((float) height
					/ (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);

			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}

		return inSampleSize;
	}

	/**
	 * 获得压缩过后的图片
	 * 
	 * @param path
	 *            文件路径
	 * @return
	 */
	public static Bitmap compressImage(String path) {

		if (null == path || path.contains("null")) {
			return null;
		}
		Bitmap bitmap = null;

		BitmapFactory.Options Boptions = new BitmapFactory.Options();
		Boptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, Boptions);
		Boptions.inSampleSize = calculateInSampleSize(Boptions, 480, 800);
		Boptions.inPurgeable = true;
		Boptions.inInputShareable = true;
		Boptions.inJustDecodeBounds = false;

		bitmap = BitmapFactory.decodeFile(path, Boptions);
		int degree = readPictureDegree(path);
		if (degree != 0) {
			bitmap = rotaingImageView(degree, bitmap);// 传入压缩后的图片进行旋转
		}
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);

		int size1 = 100;// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中

		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩

			baos.reset();// 重置baos即清空baos
			size1 -= 10;// 每次都减少10
			if (size1 == 0) {
				size1 = 1;
			}

			bitmap.compress(Bitmap.CompressFormat.JPEG, size1, baos);
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		bitmap = BitmapFactory.decodeStream(isBm);
		return bitmap;
	}

	//
	// /**
	// * 获得压缩过后的图片
	// *
	// * @param path
	// * 文件路径
	// * @return
	// */
	// public static Bitmap compressImage(String path) {
	//
	// if (null == path || path.contains("null")) {
	// return null;
	// }
	// Bitmap bitmap = null;
	//
	// BitmapFactory.Options Boptions = new BitmapFactory.Options();
	// Boptions.inJustDecodeBounds = true;
	// BitmapFactory.decodeFile(path, Boptions);
	// Boptions.inSampleSize = calculateInSampleSize(Boptions, 480, 800);
	// Boptions.inPurgeable = true;
	// Boptions.inInputShareable = true;
	// Boptions.inJustDecodeBounds = false;
	//
	// bitmap = BitmapFactory.decodeFile(path, Boptions);
	// int degree = readPictureDegree(path);
	// if (degree != 0) {
	// bitmap = rotaingImageView(degree, bitmap);// 传入压缩后的图片进行旋转
	// }
	// ByteArrayOutputStream baos = new ByteArrayOutputStream();
	//
	// bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
	//
	// int size1 = 100;// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
	//
	// while (baos.toByteArray().length / 1024 > 100) { //
	// 循环判断如果压缩后图片是否大于100kb,大于继续压缩
	//
	// baos.reset();// 重置baos即清空baos
	// size1 -= 10;// 每次都减少10
	// if (size1 == 0) {
	// size1 = 1;
	// }
	//
	// bitmap.compress(Bitmap.CompressFormat.JPEG, size1, baos);
	// }
	// ByteArrayInputStream isBm = new
	// ByteArrayInputStream(baos.toByteArray());//
	// 把压缩后的数据baos存放到ByteArrayInputStream中
	// bitmap = BitmapFactory.decodeStream(isBm);
	// return bitmap;
	// }

	/**
	 * 转换图片成圆形
	 * 
	 * @param bitmap
	 *            传入Bitmap对象
	 * @return
	 */
	// public static Bitmap toRoundBitmap(Bitmap bitmap) {
	// int width = bitmap.getWidth();
	// int height = bitmap.getHeight();
	// float roundPx;
	// float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
	// if (width <= height) {
	// roundPx = width / 2;
	// left = 0;
	// top = 0;
	// right = width;
	// bottom = width;
	// height = width;
	// dst_left = 0;
	// dst_top = 0;
	// dst_right = width;
	// dst_bottom = width;
	// } else {
	// roundPx = height / 2;
	// float clip = (width - height) / 2;
	// left = clip;
	// right = width - clip;
	// top = 0;
	// bottom = height;
	// width = height;
	// dst_left = 0;
	// dst_top = 0;
	// dst_right = height;
	// dst_bottom = height;
	// }
	//
	// Bitmap output = Bitmap.createBitmap(width, height, Config.ARGB_8888);
	// Canvas canvas = new Canvas(output);
	//
	// final int color = 0xff424242;
	// final Paint paint = new Paint();
	// final Rect src = new Rect((int) left, (int) top, (int) right,
	// (int) bottom);
	// final Rect dst = new Rect((int) dst_left, (int) dst_top,
	// (int) dst_right, (int) dst_bottom);
	//
	// paint.setAntiAlias(true);// 设置画笔无锯齿
	//
	// canvas.drawARGB(0, 0, 0, 0); // 填充整个Canvas
	// paint.setColor(color);
	//
	// // 以下有两种方法画圆,drawRounRect和drawCircle
	// // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);//
	// // 画圆角矩形，第一个参数为图形显示区域，第二个参数和第三个参数分别是水平圆角半径和垂直圆角半径。
	// canvas.drawCircle(roundPx, roundPx, roundPx, paint);
	//
	// paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));//
	// 设置两张图片相交时的模式,参考http://trylovecatch.iteye.com/blog/1189452
	// canvas.drawBitmap(bitmap, src, dst, paint); //
	// 以Mode.SRC_IN模式合并bitmap和已经draw了的Circle
	//
	// return output;
	// }

	// public static void compressBmpToFile(Bitmap bmp, File file) {
	// ByteArrayOutputStream baos = new ByteArrayOutputStream();
	// int options = 80;// 个人喜欢从80开始,
	// bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
	// while (baos.toByteArray().length / 1024 > 100) {
	// baos.reset();
	// options -= 10;
	// bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);
	// }
	// try {
	// FileOutputStream fos = new FileOutputStream(file);
	// fos.write(baos.toByteArray());
	// fos.flush();
	// fos.close();
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	// ****************this all modify by lilifeng
	// ***************************************************************************************************************************
	// public static String bitmap_too_File(String path, String name) {
	// Bitmap bit = getimage(path);
	//
	// if (null == bit) {
	// return null;
	// }
	// File file = null;
	// try {
	// File pathFile = new File(filePath);
	// if (!pathFile.exists()) {
	// pathFile.mkdirs();
	// }
	// file = new File(filePath, name);
	// if (file.exists()) {
	// file.delete();
	// }
	//
	// file.createNewFile();
	//
	// FileOutputStream fileOut = new FileOutputStream(file);
	// int size = 100;
	// if (bit.getHeight() > 1000 || bit.getWidth() > 1000) {
	// size = 80;
	// }
	// bit.compress(Bitmap.CompressFormat.JPEG, size, fileOut);
	//
	// fileOut.flush();
	// fileOut.close();
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// return file.getAbsolutePath();
	//
	// }

	// public static Bitmap compressImage(Bitmap image) {
	//
	// ByteArrayOutputStream baos = new ByteArrayOutputStream();
	// image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//
	// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
	// int options = 100;
	// while (baos.toByteArray().length / 1024 > 100) { //
	// 循环判断如果压缩后图片是否大于100kb,大于继续压缩
	// baos.reset();// 重置baos即清空baos
	// image.compress(Bitmap.CompressFormat.JPEG, options, baos);//
	// 这里压缩options%，把压缩后的数据存放到baos中
	// options -= 10;// 每次都减少10
	// }
	// System.out.println("图片压缩之后的大小" + baos.toByteArray().length / 1024);
	// ByteArrayInputStream isBm = new
	// ByteArrayInputStream(baos.toByteArray());//
	// 把压缩后的数据baos存放到ByteArrayInputStream中
	// Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//
	// 把ByteArrayInputStream数据生成图片
	// return bitmap;
	// }
	//
	// public static Bitmap getimage(String srcPath) {
	// BitmapFactory.Options newOpts = new BitmapFactory.Options();
	// // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
	// newOpts.inJustDecodeBounds = true;
	// Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空
	//
	// newOpts.inJustDecodeBounds = false;
	// int w = newOpts.outWidth;
	// int h = newOpts.outHeight;
	// // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
	// float hh = 160f;// 这里设置高度为800f
	// float ww = 240f;// 这里设置宽度为480f
	// // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
	// int be = 1;// be=1表示不缩放
	// if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
	// be = (int) (newOpts.outWidth / ww);
	// } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
	// be = (int) (newOpts.outHeight / hh);
	// }
	// if (be <= 0)
	// be = 1;
	// newOpts.inSampleSize = be;// 设置缩放比例
	// // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
	// bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
	// return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	// }
	//
	// public static Bitmap comp(Bitmap image) {
	//
	// ByteArrayOutputStream baos = new ByteArrayOutputStream();
	// image.compress(Bitmap.CompressFormat.JPEG, 90, baos);
	// while (baos.toByteArray().length / 1024 > 100) {//
	// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
	// baos.reset();// 重置baos即清空baos
	// image.compress(Bitmap.CompressFormat.JPEG, 90, baos);//
	// 这里压缩50%，把压缩后的数据存放到baos中
	//
	// }
	// ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
	// BitmapFactory.Options newOpts = new BitmapFactory.Options();
	// // 开始读入图片，此时把options.inJustDecodeBounds 设回true了
	// newOpts.inJustDecodeBounds = true;
	// Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
	// newOpts.inJustDecodeBounds = false;
	// int w = newOpts.outWidth;
	// int h = newOpts.outHeight;
	// // 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
	// float hh = 160f;// 这里设置高度为800f
	// float ww = 240f;// 这里设置宽度为480f
	// // 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
	// int be = 1;// be=1表示不缩放
	// if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
	// be = (int) (newOpts.outWidth / ww);
	// } else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
	// be = (int) (newOpts.outHeight / hh);
	// }
	// if (be <= 0)
	// be = 1;
	// newOpts.inSampleSize = be;// 设置缩放比例
	// // 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
	// isBm = new ByteArrayInputStream(baos.toByteArray());
	// bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
	// return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	// }

	/**
	 * 根据Uri获取图片绝对路径，解决Android4.4以上版本Uri转换
	 * 
	 * @param context
	 * @param imageUri
	 * @author yaoxing
	 * @date 2014-10-12
	 */
	@TargetApi(19)
	public static String getImageAbsolutePath(Activity context, Uri imageUri) {
		if (context == null || imageUri == null)
			return null;
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT
				&& DocumentsContract.isDocumentUri(context, imageUri)) {
			if (isExternalStorageDocument(imageUri)) {
				String docId = DocumentsContract.getDocumentId(imageUri);
				String[] split = docId.split(":");
				String type = split[0];
				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/"
							+ split[1];
				}
			} else if (isDownloadsDocument(imageUri)) {
				String id = DocumentsContract.getDocumentId(imageUri);
				Uri contentUri = ContentUris.withAppendedId(
						Uri.parse("content://downloads/public_downloads"),
						Long.valueOf(id));
				return getDataColumn(context, contentUri, null, null);
			} else if (isMediaDocument(imageUri)) {
				String docId = DocumentsContract.getDocumentId(imageUri);
				String[] split = docId.split(":");
				String type = split[0];
				Uri contentUri = null;
				if ("image".equals(type)) {
					contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}
				String selection = MediaStore.Images.Media._ID + "=?";
				String[] selectionArgs = new String[] { split[1] };
				return getDataColumn(context, contentUri, selection,
						selectionArgs);
			}
		} // MediaStore (and general)
		else if ("content".equalsIgnoreCase(imageUri.getScheme())) {
			// Return the remote address
			if (isGooglePhotosUri(imageUri))
				return imageUri.getLastPathSegment();
			return getDataColumn(context, imageUri, null, null);
		}
		// File
		else if ("file".equalsIgnoreCase(imageUri.getScheme())) {
			return imageUri.getPath();
		}
		return null;
	}

	public static void deleteFile(File file){
		if(file!=null){
			file.delete();
		}
	}
	public static final long LIMIT_SIZE=1024*256;
	public static final int LIMIT_WIDTH=500;//压缩的图片均以宽为准，超出这个宽度的，都按宽等比压缩至这么大。
	public static File compressImage(File file){
		if(file==null||!file.exists())return null;
		long size=file.length();
		BitmapFactory.Options options=new BitmapFactory.Options();
		options.inJustDecodeBounds=true;
		Bitmap bm;
		bm=BitmapFactory.decodeFile(file.getPath(),options);
		int W=options.outWidth;
		int H=options.outHeight;
		Log.d("img","start compress ------dimen w="+W+",h="+H);
		int degree = ImageUtil.readPictureDegree(file.getPath());
		//以角度为0或者180的宽为基准宽
		int baseW=0;
		if(degree==0||degree==180){
			baseW=W;
		}else {
			baseW=H;
		}
		if(size>LIMIT_SIZE||baseW>LIMIT_SIZE){
			bm=scaleDimen(file,degree,baseW,options);
			long scale=size/LIMIT_SIZE;
//            double scale=Math.sqrt(((double) size)/(double)LIMIT_SIZE);
			String newPath=filePath+System.currentTimeMillis()+"_temp_compress.png";

			File newFile=new File(newPath);
			FileOutputStream os=null;
			try {
				os=new FileOutputStream(newFile);
				boolean isSuc=bm.compress(Bitmap.CompressFormat.PNG,size>LIMIT_SIZE?(int)(100.0f/(float) scale):100,os);
				Log.d("img","compress is success?--"+isSuc+"--size="+newFile.length()+"---old size="+size+"----dimen w="+bm.getWidth()+",h="+bm.getHeight());
				return newFile;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}finally {
				if(os!=null) try {
					os.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}else {
			String suffix=file.getName().split("\\.")[1];
			String newPath=filePath+System.currentTimeMillis()+"_temp_compress"+(suffix.length()>0?"."+suffix:"");
			File newFile=new File(newPath);
			if(newFile.exists()){
				newFile.delete();
			}
			try {
				newFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			copyFile(file.getAbsolutePath(),newPath);
			return new File(newPath);
		}
		return null;
	}

	//缩放图片至500p
	private static Bitmap scaleDimen(File file,int degree,int W,BitmapFactory.Options options){
		options.inJustDecodeBounds=false;
		Bitmap bm;
		if(W>LIMIT_WIDTH){
			float scale=(float)W/(float)LIMIT_WIDTH;
			options.inMutable=true;
			options.inScaled=true;
			options.inSampleSize=(int)(scale+0.5);//四舍五入
			options.inPurgeable = true;// 同时设置才会有效
			options.inInputShareable = true;//。当系统内存不够时候图片自动被回收
			bm=BitmapFactory.decodeFile(file.getPath(),options);
		}else {
			bm=BitmapFactory.decodeFile(file.getPath());
		}
		//把图片旋转为正方向
		try {
			if (degree != 0) {
				bm = ImageUtil.rotaingImageView(degree, bm);// 传入压缩后的图片进行旋转
			}
		}catch (OutOfMemoryError e){
			e.printStackTrace();
		}
		return bm;
	}

	public static void copyFile(String oldPath, String newPath) {
		Log.d("copy_file","开始复制");
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { //文件不存在时
				InputStream inStream = new FileInputStream(oldPath); //读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				int length;
				while ( (byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; //字节数 文件大小
					System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
			Log.d("copy_file",newPath+"复制成功--"+new File(newPath).exists());
		}
		catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();

		}

	}

	public static File saveImag(Bitmap bitmap, String name) {

		// Bitmap bitmap = compressImage(path);
		// // Bitmap bitmap = getimage_(path);

		if (null == bitmap) {
			return null;
		}
		File file = null;
		try {

			file = new File(filePath, name);
			if (file.exists()) {
				file.delete();
			}

			file.createNewFile();

			FileOutputStream fileOut = new FileOutputStream(file);
			int size = 100;
			if (bitmap.getHeight() > 1000 || bitmap.getWidth() > 1000) {
				size = 80;
			}
			bitmap.compress(Bitmap.CompressFormat.PNG, size, fileOut);

			fileOut.flush();
			fileOut.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
		return file;
	}

	public static String getRealPathFromURI(Context context,Uri contentUri) {
		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, contentUri)) {
			if (isExternalStorageDocument(contentUri)) {
				String docId = DocumentsContract.getDocumentId(contentUri);
				String[] split = docId.split(":");
				String type = split[0];
				if ("primary".equalsIgnoreCase(type)) {
					return Environment.getExternalStorageDirectory() + "/" + split[1];
				}
			} else if (isDownloadsDocument(contentUri)) {
				String id = DocumentsContract.getDocumentId(contentUri);
				Uri contentUri2 = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
				return getDataColumn(context, contentUri2, null, null);
			} else if (isMediaDocument(contentUri)) {
				String docId = DocumentsContract.getDocumentId(contentUri);
				String[] split = docId.split(":");
				String type = split[0];
				Uri contentUri2 = null;
				if ("image".equals(type)) {
					contentUri2 = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				} else if ("video".equals(type)) {
					contentUri2 = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
				} else if ("audio".equals(type)) {
					contentUri2 = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
				}
				String selection = MediaStore.Images.Media._ID + "=?";
				String[] selectionArgs = new String[] { split[1] };
				return getDataColumn(context, contentUri2, selection, selectionArgs);
			}
		}else if(ContentResolver.SCHEME_CONTENT.equals(contentUri.getScheme())){
			String res = null;
			String[] proj = { MediaStore.Images.Media.DATA };
			Cursor cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
			if(cursor.moveToFirst()){;
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				res = cursor.getString(column_index);
			}
			cursor.close();
			return res;
		}else{
			String path=contentUri.toString();
			if(path.startsWith("file")){
				path=path.replace("file://","");
			}
			return path;
		}
		return null;
	}

	public static String getDataColumn(Context context, Uri uri, String selection, String[] selectionArgs) {
		Cursor cursor = null;
		String column = MediaStore.Images.Media.DATA;
		String[] projection = { column };
		try {
			cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
			if (cursor != null && cursor.moveToFirst()) {
				int index = cursor.getColumnIndexOrThrow(column);
				return cursor.getString(index);
			}
		} finally {
			if (cursor != null)
				cursor.close();
		}
		return null;
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is ExternalStorageProvider.
	 */
	public static boolean isExternalStorageDocument(Uri uri) {
		return "com.android.externalstorage.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is DownloadsProvider.
	 */
	public static boolean isDownloadsDocument(Uri uri) {
		return "com.android.providers.downloads.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is MediaProvider.
	 */
	public static boolean isMediaDocument(Uri uri) {
		return "com.android.providers.media.documents".equals(uri.getAuthority());
	}

	/**
	 * @param uri The Uri to check.
	 * @return Whether the Uri authority is Google Photos.
	 */
	public static boolean isGooglePhotosUri(Uri uri) {
		return "com.google.android.apps.photos.content".equals(uri.getAuthority());
	}



	public static File saveImageToGallery(Context context, Bitmap bmp) {
		// 首先保存图片
		File appDir = new File(filePath);
		if (!appDir.exists()) {
			appDir.mkdir();
		}
		String fileName = System.currentTimeMillis() + ".jpg";
		File file = new File(appDir, fileName);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}

		// 其次把文件插入到系统图库
		try {
			MediaStore.Images.Media.insertImage(context.getContentResolver(),
					file.getAbsolutePath(), fileName, null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		// 最后通知图库更新
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + file.getPath())));
		return file;
	}


}
