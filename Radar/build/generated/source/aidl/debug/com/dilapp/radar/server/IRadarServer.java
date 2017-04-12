/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/huangwei/StudioProjects/Radar_Code_grad/Radar/src/com/dilapp/radar/server/IRadarServer.aidl
 */
package com.dilapp.radar.server;
public interface IRadarServer extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.dilapp.radar.server.IRadarServer
{
private static final java.lang.String DESCRIPTOR = "com.dilapp.radar.server.IRadarServer";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.dilapp.radar.server.IRadarServer interface,
 * generating a proxy if needed.
 */
public static com.dilapp.radar.server.IRadarServer asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.dilapp.radar.server.IRadarServer))) {
return ((com.dilapp.radar.server.IRadarServer)iin);
}
return new com.dilapp.radar.server.IRadarServer.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_registerCallback:
{
data.enforceInterface(DESCRIPTOR);
com.dilapp.radar.server.IRadarCallback _arg0;
_arg0 = com.dilapp.radar.server.IRadarCallback.Stub.asInterface(data.readStrongBinder());
this.registerCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unRegisterCallback:
{
data.enforceInterface(DESCRIPTOR);
this.unRegisterCallback();
reply.writeNoException();
return true;
}
case TRANSACTION_startTestScript:
{
data.enforceInterface(DESCRIPTOR);
int _arg0;
_arg0 = data.readInt();
this.startTestScript(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_startUploadServer:
{
data.enforceInterface(DESCRIPTOR);
ServerRequestParams _arg0;
if ((0!=data.readInt())) {
_arg0 = ServerRequestParams.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
int _arg1;
_arg1 = data.readInt();
this.startUploadServer(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_startLocalData:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
int _arg2;
_arg2 = data.readInt();
this.startLocalData(_arg0, _arg1, _arg2);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.dilapp.radar.server.IRadarServer
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void registerCallback(com.dilapp.radar.server.IRadarCallback callback) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((callback!=null))?(callback.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void unRegisterCallback() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_unRegisterCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void startTestScript(int script) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeInt(script);
mRemote.transact(Stub.TRANSACTION_startTestScript, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void startUploadServer(ServerRequestParams requestParams, int callBackId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((requestParams!=null)) {
_data.writeInt(1);
requestParams.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
_data.writeInt(callBackId);
mRemote.transact(Stub.TRANSACTION_startUploadServer, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void startLocalData(java.lang.String localRequestParams, java.lang.String localContent, int callBackId) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(localRequestParams);
_data.writeString(localContent);
_data.writeInt(callBackId);
mRemote.transact(Stub.TRANSACTION_startLocalData, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_registerCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_unRegisterCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_startTestScript = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_startUploadServer = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_startLocalData = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
}
public void registerCallback(com.dilapp.radar.server.IRadarCallback callback) throws android.os.RemoteException;
public void unRegisterCallback() throws android.os.RemoteException;
public void startTestScript(int script) throws android.os.RemoteException;
public void startUploadServer(ServerRequestParams requestParams, int callBackId) throws android.os.RemoteException;
public void startLocalData(java.lang.String localRequestParams, java.lang.String localContent, int callBackId) throws android.os.RemoteException;
}
