package boggledApp;

/**
* boggledApp/usernameAlreadyExistsExceptionHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from corbaIDL.idl
* Tuesday, May 28, 2024 5:11:50 PM SGT
*/

public final class usernameAlreadyExistsExceptionHolder implements org.omg.CORBA.portable.Streamable
{
  public boggledApp.usernameAlreadyExistsException value = null;

  public usernameAlreadyExistsExceptionHolder ()
  {
  }

  public usernameAlreadyExistsExceptionHolder (boggledApp.usernameAlreadyExistsException initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = boggledApp.usernameAlreadyExistsExceptionHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    boggledApp.usernameAlreadyExistsExceptionHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return boggledApp.usernameAlreadyExistsExceptionHelper.type ();
  }

}
