package boggledApp;


/**
* boggledApp/roomDoesntExistException.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from corbaIDL.idl
* Tuesday, May 28, 2024 5:11:50 PM SGT
*/

public final class roomDoesntExistException extends org.omg.CORBA.UserException
{
  public String msg = null;

  public roomDoesntExistException ()
  {
    super(roomDoesntExistExceptionHelper.id());
  } // ctor

  public roomDoesntExistException (String _msg)
  {
    super(roomDoesntExistExceptionHelper.id());
    msg = _msg;
  } // ctor


  public roomDoesntExistException (String $reason, String _msg)
  {
    super(roomDoesntExistExceptionHelper.id() + "  " + $reason);
    msg = _msg;
  } // ctor

} // class roomDoesntExistException
