package boggledApp;


/**
* boggledApp/userAlreadyLogInException.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from corbaIDL.idl
* Tuesday, May 28, 2024 5:11:50 PM SGT
*/

public final class userAlreadyLogInException extends org.omg.CORBA.UserException
{
  public String msg = null;

  public userAlreadyLogInException ()
  {
    super(userAlreadyLogInExceptionHelper.id());
  } // ctor

  public userAlreadyLogInException (String _msg)
  {
    super(userAlreadyLogInExceptionHelper.id());
    msg = _msg;
  } // ctor


  public userAlreadyLogInException (String $reason, String _msg)
  {
    super(userAlreadyLogInExceptionHelper.id() + "  " + $reason);
    msg = _msg;
  } // ctor

} // class userAlreadyLogInException
