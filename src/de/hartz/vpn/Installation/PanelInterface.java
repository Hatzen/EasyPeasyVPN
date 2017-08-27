package de.hartz.vpn.Installation;

/**
 * Created by kaiha on 01.06.2017.
 */
public interface PanelInterface {

    void onSelect();

    /**
     *
     * @return boolean indicating whether deselecting is possible.
     */
    boolean onDeselect();

}
