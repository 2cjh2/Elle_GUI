package com.elle.ellegui.presentation.filter;
import javax.swing.*;
import java.util.*;

/**
 * Default model for check list. It is based on the list of items
 * Implementation of checks is based on HashSet of checked items
 *
 * @author Eugene Ryzhikov
 *
 * @param <T> list element type
 */
public class DefaultCheckListModel<T> extends AbstractListModel implements ICheckListModel<T> {

    private static final long serialVersionUID = 1L;

    public static IObjectToStringTranslator DEFAULT_TRANSLATOR = new DefaultObjectToStringTranslator();

    private Set<T> checks = new HashSet<T>();
    private final List<T> dataList = new ArrayList<T>();
    private final Set<T> dataSet = new HashSet<T>();
    private List<T> filteredDataList = null;
    private Set<T> filteredDataSet = null;

    public DefaultCheckListModel( Collection<? extends T> data ) {

        if ( data == null ) return;
        for (T object : data) {
            dataList.add(object);
            dataSet.add(object);
        }
    }

    public DefaultCheckListModel( T... data ) {
        this( Arrays.asList( data ));
    }

    /* (non-Javadoc)
     * @see org.oxbow.swingbits.list.ICheckListModel#getSize()
     */
    @Override
    public int getSize() {
        return dataList().size();
    }

    private List<T> dataList() {
        return filteredDataList == null ? dataList : filteredDataList;
    }

    private Set<T> dataSet() {
        return filteredDataSet == null ? dataSet : filteredDataSet;
    }

    /* (non-Javadoc)
     * @see org.oxbow.swingbits.list.ICheckListModel#getElementAt(int)
     */
    @Override
    public Object getElementAt(int index) {
        return dataList().get(index);
    }

    /* (non-Javadoc)
     * @see org.oxbow.swingbits.list.ICheckListModel#isChecked(int)
     */
    @Override
    public boolean isCheckedIndex( int index ) {
        return checks.contains(dataList().get(index));
    }

    /* (non-Javadoc)
     * @see org.oxbow.swingbits.list.ICheckListModel#setChecked(int, boolean)
     */
    @Override
    public void setCheckedIndex( int index, boolean value ) {
        T o = dataList().get(index);
        if ( value ) checks.add(o); else checks.remove(o);
        fireContentsChanged(this, index, index);
    }

    /* (non-Javadoc)
     * @see org.oxbow.swingbits.list.ICheckListModel#getChecked()
     */
    @Override
    public Collection<T> getCheckedItems() {
        List<T> items = new ArrayList<T>(checks);
        items.retainAll(dataSet());
        return Collections.unmodifiableList( items );
    }

    /* (non-Javadoc)
     * @see org.oxbow.swingbits.list.ICheckListModel#setChecked(java.util.Collection)
     */
    @Override
    public void setCheckedItems( Collection<T> items ) {
        Set<T> correctedItems = new HashSet<T>(items);
        correctedItems.retainAll(dataSet());
        checks = correctedItems;
        fireContentsChanged(this, 0, checks.size()-1);
    }

    public void filter( String pattern, IObjectToStringTranslator translator, IListFilter listFilter ) {

        if ( pattern == null || pattern.trim().length() == 0 ) {
            filteredDataList = null;
            filteredDataSet = null;
        } else {

            IListFilter filter = listFilter == null? CheckListFilterType.CONTAINS: listFilter;

            IObjectToStringTranslator t = translator == null? DEFAULT_TRANSLATOR: translator;
            String p = pattern.toLowerCase();

            List<T> fDataList = new ArrayList<T>();
            Set<T> fDataSet = new HashSet<T>();

            Object value;
            for (T o : dataList) {
                //if ( t.translate(o).startsWith(f)) {
                value = o instanceof IValueWrapper? ((IValueWrapper<?>)o).getValue(): o;
                if ( filter.include(t.translate(value), p)) {
                    fDataList.add(o);
                    fDataSet.add(o);
                }
            }
            filteredDataList = fDataList;
            filteredDataSet = fDataSet;
        }

        fireContentsChanged(this, 0, dataList.size() - 1);

    }


}