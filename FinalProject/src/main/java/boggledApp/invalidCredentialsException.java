package boggledApp;


/**
* boggledApp/invalidCredentialsException.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from corbaIDL.idl
* Tuesday, May 28, 2024 5:11:50 PM SGT
*/

public final class invalidCredentialsException extends org.omg.CORBA.UserException
{
  public String msg = null;

  public invalidCredentialsException ()
  {
    super(invalidCredentialsExceptionHelper.id());
  } // ctor

  public invalidCredentialsException (String _msg)
  {
    super(invalidCredentialsExceptionHelper.id());
    msg = _msg;
  } // ctor


  public invalidCredentialsException (String $reason, String _msg)
  {
    super(invalidCredentialsExceptionHelper.id() + "  " + $reason);
    msg = _msg;
  } // ctor

} // class invalidCredentialsException
