package com.magnifyingglass.magnifiercamera.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import androidx.activity.ComponentActivity
import androidx.databinding.ViewDataBinding
import androidx.viewbinding.ViewBinding
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.ParameterizedType


fun <VB : ViewBinding> LayoutInflater.inflateWithGeneric(genericOwner: Any): VB =
    withGenericBindingClass(genericOwner) { clazz ->
        clazz.getMethod("inflate", LayoutInflater::class.java)
            .invoke(null, this) as VB
    }.also { binding ->
        if (genericOwner is ComponentActivity && binding is ViewDataBinding) {
            binding.lifecycleOwner = genericOwner
        }
    }

private fun <VB : ViewBinding> withGenericBindingClass(
    genericOwner: Any,
    block: (Class<VB>) -> VB
): VB {
    var genericSuperclass = genericOwner.javaClass.genericSuperclass
    var superclass = genericOwner.javaClass.superclass
    while (superclass != null) {
        if (genericSuperclass is ParameterizedType) {
            genericSuperclass.actualTypeArguments.forEach {
                try {
                    return block.invoke(it as Class<VB>)
                } catch (e: NoSuchMethodException) {
                } catch (e: ClassCastException) {
                } catch (e: InvocationTargetException) {
                    var tagException: Throwable? = e
                    while (tagException is InvocationTargetException) {
                        tagException = e.cause
                    }
                    throw tagException
                        ?: IllegalArgumentException("ViewBinding generic was found, but creation failed.")
                }
            }
        }
        genericSuperclass = superclass.genericSuperclass
        superclass = superclass.superclass
    }
    throw IllegalArgumentException("There is no generic of ViewBinding.")
}
fun String.goToMarket(context: Context) {
    val uri = Uri.parse("market://details?id=$this")
    val goToMarket = Intent(Intent.ACTION_VIEW, uri).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    try {
        context.startActivity(goToMarket)
    } catch (e: ActivityNotFoundException) {
        e.printStackTrace()
    }
}

fun String.shareToFriend(context: Context) {
    val text = "https://play.google.com/store/apps/details?id=$this"
    val shareToFriendIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "")
        putExtra(Intent.EXTRA_TEXT, text)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    val chooserIntent = Intent.createChooser(shareToFriendIntent, "Share to").apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(chooserIntent)
}

fun String.sendMail(context: Context) {
    val mailTo = arrayOf(this)
    val emailIntent = Intent(Intent.ACTION_SEND).apply {
        data = Uri.parse("mailto:")
        type = "text/plain"
        putExtra(Intent.EXTRA_EMAIL, mailTo)
        putExtra(Intent.EXTRA_SUBJECT, "")
        putExtra(Intent.EXTRA_TEXT, "")
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    val chooserIntent = Intent.createChooser(emailIntent, "Send mail").apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(chooserIntent)
}