package boggledApp;

/**
* boggledApp/mapTimeAndIDHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from corbaIDL.idl
* Sunday, May 12, 2024 3:04:18 PM SGT
*/

public final class mapTimeAndIDHolder implements org.omg.CORBA.portable.Streamable
{
  public boggledApp.mapTimeAndID value = null;

  public mapTimeAndIDHolder ()
  {
  }

  public mapTimeAndIDHolder (boggledApp.mapTimeAndID initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = boggledApp.mapTimeAndIDHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    boggledApp.mapTimeAndIDHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return boggledApp.mapTimeAndIDHelper.type ();
  }

}
