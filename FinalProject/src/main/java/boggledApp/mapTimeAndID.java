package boggledApp;


/**
* boggledApp/mapTimeAndID.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from corbaIDL.idl
* Sunday, May 12, 2024 3:04:18 PM SGT
*/

public final class mapTimeAndID implements org.omg.CORBA.portable.IDLEntity
{
  public int roomID = (int)0;
  public int remainingTime = (int)0;

  public mapTimeAndID ()
  {
  } // ctor

  public mapTimeAndID (int _roomID, int _remainingTime)
  {
    roomID = _roomID;
    remainingTime = _remainingTime;
  } // ctor

} // class mapTimeAndID
