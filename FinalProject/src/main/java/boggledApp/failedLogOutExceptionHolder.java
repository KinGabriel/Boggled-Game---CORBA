package boggledApp;

/**
* boggledApp/failedLogOutExceptionHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from corbaIDL.idl
* Tuesday, May 28, 2024 5:11:50 PM SGT
*/

public final class failedLogOutExceptionHolder implements org.omg.CORBA.portable.Streamable
{
  public boggledApp.failedLogOutException value = null;

  public failedLogOutExceptionHolder ()
  {
  }

  public failedLogOutExceptionHolder (boggledApp.failedLogOutException initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = boggledApp.failedLogOutExceptionHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    boggledApp.failedLogOutExceptionHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return boggledApp.failedLogOutExceptionHelper.type ();
  }

}
