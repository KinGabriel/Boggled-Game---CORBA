package boggledApp;

/**
* boggledApp/invalidCredentialsExceptionHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from corbaIDL.idl
* Tuesday, May 28, 2024 5:11:50 PM SGT
*/

public final class invalidCredentialsExceptionHolder implements org.omg.CORBA.portable.Streamable
{
  public boggledApp.invalidCredentialsException value = null;

  public invalidCredentialsExceptionHolder ()
  {
  }

  public invalidCredentialsExceptionHolder (boggledApp.invalidCredentialsException initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = boggledApp.invalidCredentialsExceptionHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    boggledApp.invalidCredentialsExceptionHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return boggledApp.invalidCredentialsExceptionHelper.type ();
  }

}
