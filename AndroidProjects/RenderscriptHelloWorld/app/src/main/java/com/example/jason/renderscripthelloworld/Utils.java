package com.example.jason.renderscripthelloworld;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;

class Utils {

    static void replaceFragment(FragmentManager fragmentManager, int containerViewId,
                                Fragment fragment, String fragmentTag, boolean addToBackStack) {

        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(containerViewId, fragment, fragmentTag);

        if (addToBackStack) {
            fragmentTransaction.addToBackStack(fragmentTag);
        }

        fragmentTransaction.commit();
    }

    static Bitmap histogramEqualization(Bitmap image, Context context) {
        // Get image size
        int width = image.getWidth();
        int height = image.getHeight();

        // Create new bitmap
        Bitmap bitmap = image.copy(image.getConfig(), true /* isMutable */);

        // Create renderscript
        RenderScript renderScript = RenderScript.create(context);

        // Create allocation from Bitmap
        Allocation allocationA = Allocation.createFromBitmap(renderScript, bitmap);

        // Create allocation with same type
        Allocation allocationB = Allocation.createTyped(renderScript, allocationA.getType());

        // Create script from renderscript file
        ScriptC_histogramEqualizer histogramEqualizer = new ScriptC_histogramEqualizer(renderScript);

        // Set size in script
        histogramEqualizer.set_size(width * height);

        // Call the first kernel
        histogramEqualizer.forEach_root(allocationA, allocationB);

        // Call the renderscript method to compute the remap array
        histogramEqualizer.invoke_createRemapArray();

        // Call second kernel
        histogramEqualizer.forEach_remaptoRGB(allocationB, allocationA);

        // Copy script result into our bitmap
        allocationA.copyTo(bitmap);

        // De-allocate memory usage
        allocationA.destroy();
        allocationB.destroy();
        histogramEqualizer.destroy();
        renderScript.destroy();

        return bitmap;
    }

    public void slowEqualize(Bitmap src) {
        float histogram[][];
        histogram = new float[3][];

        histogram[0] = getHistogramByColor(src, 1);
        histogram[1] = getHistogramByColor(src, 2);
        histogram[2] = getHistogramByColor(src, 3);

        normalizedFunction(histogram[0], 0, histogram[0].length - 1);
        normalizedFunction(histogram[1], 0, histogram[0].length - 1);
        normalizedFunction(histogram[2], 0, histogram[0].length - 1);

        javaHistogramEqualization(histogram[0], 0, 255);
        javaHistogramEqualization(histogram[1], 0, 255);
        javaHistogramEqualization(histogram[2], 0, 255);
    }

    private void javaHistogramEqualization(float histogram[], int low, int high) {

        float sumr, sumrx;
        sumr = 0;
        for (int i = low; i <= high; i++) {
            sumr += (histogram[i]);
            sumrx = low + (high - low) * sumr;
            int valr = (int) (sumrx);
            if (valr > 255) {
                histogram[i] = 255;
            } else {
                histogram[i] = valr;
            }
        }
    }

    private void normalizedFunction(float myArr[], int low, int high) {

        float sumV = 0.0f;
        for (int i = low; i <= high; i++) {
            sumV = sumV + (myArr[i]);
        }
        for (int i = low; i <= high; i++) {
            myArr[i] /= sumV;
        }
    }

    private float[] getHistogramByColor(Bitmap input, int colorVal) {
        // colorVal 1 -> RED     2 -> GREEN     3 -> BLUE
        float[] histogram = new float[256];

        for (int i = 0; i < histogram.length; i++) {
            histogram[i] = 0.0f;
        }
        for (int i = 0; i < input.getWidth(); i++) {
            for (int j = 0; j < input.getHeight(); j++) {
                int red = 0;
                switch (colorVal) {
                    case 1:
                        red = Color.red(input.getPixel(i, j));
                        break;
                    case 2:
                        red = Color.green(input.getPixel(i, j));
                        break;
                    case 3:
                        red = Color.blue(input.getPixel(i, j));
                        break;
                }
                histogram[red]++;
            }
        }
        return histogram;
    }

    static Bitmap gaussianBlur(Bitmap image, Context context, float blurRadius) {
        final RenderScript rs = RenderScript.create(context);
        final Allocation input = Allocation.createFromBitmap( rs, image, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT );
        final Allocation output = Allocation.createTyped( rs, input.getType() );
        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create( rs, Element.U8_4( rs ) );
        script.setRadius(blurRadius);
        script.setInput(input);
        script.forEach(output);
        output.copyTo(image);

        return image;
    }

    static Bitmap invert(Context context, Bitmap image) {
        RenderScript RS = RenderScript.create(context);
        ScriptC_invert script = new ScriptC_invert(RS);

        //Create allocations via our Bitmap
        Allocation inputAllocation = Allocation.createFromBitmap(RS, image);

        // Invoke our invert function
        script.invoke_process(inputAllocation);

        script.destroy();
        RS.destroy();

        return image;
    }
}
