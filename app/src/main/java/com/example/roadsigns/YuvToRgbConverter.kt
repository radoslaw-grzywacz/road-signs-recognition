package com.example.roadsigns

import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.media.Image
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicYuvToRGB
import android.renderscript.Type

/**
 * Utility to convert YUV_420_888 images from CameraX to RGB Bitmaps.
 * This implementation uses RenderScript for simplicity.
 */
class YuvToRgbConverter(context: Context) {
    private val rs: RenderScript = RenderScript.create(context)
    private val scriptYuvToRgb: ScriptIntrinsicYuvToRGB =
        ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs))
    private var yuvBytes: ByteArray? = null
    private var inputAllocation: Allocation? = null
    private var outputAllocation: Allocation? = null

    fun yuvToRgb(image: Image, output: Bitmap) {
        if (image.format != ImageFormat.YUV_420_888) {
            throw IllegalArgumentException("Unsupported image format ${image.format}")
        }
        val ySize = image.planes[0].buffer.remaining()
        val uSize = image.planes[1].buffer.remaining()
        val vSize = image.planes[2].buffer.remaining()
        val size = ySize + uSize + vSize
        if (yuvBytes == null || yuvBytes!!.size < size) {
            yuvBytes = ByteArray(size)
        }
        image.planes[0].buffer.get(yuvBytes, 0, ySize)
        image.planes[1].buffer.get(yuvBytes, ySize, uSize)
        image.planes[2].buffer.get(yuvBytes, ySize + uSize, vSize)

        if (inputAllocation == null) {
            inputAllocation = Allocation.createSized(rs, Element.U8(rs), size)
            val rgbaType = Type.Builder(rs, Element.RGBA_8888(rs))
                .setX(image.width)
                .setY(image.height)
                .create()
            outputAllocation = Allocation.createTyped(rs, rgbaType)
        }

        inputAllocation!!.copyFrom(yuvBytes)
        scriptYuvToRgb.setInput(inputAllocation)
        scriptYuvToRgb.forEach(outputAllocation)
        outputAllocation!!.copyTo(output)
    }
}
