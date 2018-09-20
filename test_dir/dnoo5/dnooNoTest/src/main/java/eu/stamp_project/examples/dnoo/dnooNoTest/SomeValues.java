package eu.stamp_project.examples.dnoo.dnooNoTest;

// **********************************************************************

// **********************************************************************
public class SomeValues
{
    // **********************************************************************
    // public
    // **********************************************************************
    // ******** attributes
    public String getName()
    {
        return(_Name);
    }

    // ********
    public void setName(String value)
    {
        _Name = value;
    }

    // **********************************************************************
    public boolean getIsUsed()
    {
        return(_IsUsed);
    }

    // ********
    public void setIsUsed(boolean value)
    {
        _IsUsed = value;
    }

    // **********************************************************************
    public int getCount()
    {
        return(_Count);
    }

    // ********
    public void setCount(int value)
    {
        _Count = value;
    }

    // **********************************************************************
    // ******** methods
    public SomeValues(int count, String name)
    {
        setName(name);
        setIsUsed(false);
        setCount(count);
    }

    // **********************************************************************
    // protected
    // **********************************************************************
    // ******** attributes
    protected String _Name;
    protected boolean _IsUsed;
    protected int _Count;
}
