package controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Circle;
//import org.jetbrains.annotations.NotNull;

public class MouvementsController {

    /**The target node to drag**/
    private final Node target;

    /**The original position of mouse in the scene when grabbing the target node**/
    private double anchorX, anchorY;

    /**The distance between the position of the mouse and the position of the node when the node is catched**/
    private double mouseOffsetFromNodeZeroX, mouseOffsetFromNodeZeroY;

    /**Event Handler on mouse pressed**/
    private EventHandler<MouseEvent> setAnchor;
    /**Event Handler on mouse dragged**/
    private EventHandler<MouseEvent> updatePositionOnDrag;
    /**Event Handler on mouse realeased**/
    private EventHandler<MouseEvent> commitPositionOnRelease;

    /**State of the dragging cycle (currently dragging or not)**/
    private final int ACTIVE = 1;
    private final int INACTIVE = 0;
    private int cycleStatus = INACTIVE;

    /**Define if the target node can be dragged**/
    private BooleanProperty isDraggable;

    /**Define the boundaries where the node can be dragged to and released into.**/
    private Bounds dragBoundary, releaseBoundary = null;

    /**Check conditions for grab, drag and release action on the target node**/
    private SimpleChecker grabChecker, dragChecker, releaseChecker = null;

   
    public MouvementsController(Node target) {
        this(target, false);
    }

   
    public MouvementsController(Node target, boolean isDraggable) {
        this.target = target;
        createHandlers();
        createDraggableProperty();

        this.isDraggable.set(isDraggable);
    }

    /*---------------------Events-----------------------*/

    /**
     * Initialise the 3 eventHandlers.
     */
    private void createHandlers() {
        setAnchor = event -> {
            if(this.grabChecker != null){
                if(this.grabChecker.check()){
                    grabHandler(event);
                }
            } else {
                grabHandler(event);
            }
        };

        updatePositionOnDrag = event -> {
            if(this.dragChecker != null){
                if(this.dragChecker.check()){
                    if (cycleStatus != INACTIVE)
                    dragHandler(event);
                }
            } else {
                if (cycleStatus != INACTIVE)
                dragHandler(event);
            }
        };

        commitPositionOnRelease = event -> {
            if(this.releaseChecker != null){
                if(this.releaseChecker.check()){
                    if (cycleStatus != INACTIVE)
                    releaseHandler(event);
                } else {
                    target.setTranslateX(0);
                    target.setTranslateY(0);
                    cycleStatus = INACTIVE;
                }
            } else {
                if (cycleStatus != INACTIVE)
                releaseHandler(event);
            }
        };
    }

    /**
     * Handler when grabbing the target node. Init the anchor and the mouseOffset
     * <br/>Check boundaries but not conditions
     * @param event mouseEvent
     */
    public void grabHandler(MouseEvent event){
        if (event.isPrimaryButtonDown()) {
            cycleStatus = ACTIVE;
            anchorX = event.getSceneX();
            anchorY = event.getSceneY();
            if(target instanceof Circle){//Use upper left corner coordonate. Circle use center coordonates
                Circle c = (Circle) target;
                mouseOffsetFromNodeZeroX = event.getX()+c.getRadius();
                mouseOffsetFromNodeZeroY = event.getY()+c.getRadius();
            } else {
                mouseOffsetFromNodeZeroX = event.getX(); //Distance to the coordinate of the node (inside the node)
                mouseOffsetFromNodeZeroY = event.getY();
            }

        }

        if (event.isSecondaryButtonDown()) {
            cycleStatus = INACTIVE;
            target.setTranslateX(0);
            target.setTranslateY(0);
        }
    }

    /**
     * Handler when dragging the target node. Modify translateX & Y of the node
     * <br/>Check boundaries but not conditions
     * @param event mouseEvent
     */
    public boolean dragHandler(MouseEvent event){
        boolean succes = false;
        if(this.dragBoundary != null){
            double oldTranslateX = target.getTranslateX();
            double oldTranslateY = target.getTranslateY();
            target.setTranslateX(event.getSceneX() - anchorX);
            target.setTranslateY(event.getSceneY() - anchorY);
            Bounds bounds = target.getBoundsInParent();
            if(! this.dragBoundary.contains(bounds)){
                target.setTranslateX(oldTranslateX);
                target.setTranslateY(oldTranslateY);
            } else {
                succes = true;
            }
        } else {
            target.setTranslateX(event.getSceneX() - anchorX);
            target.setTranslateY(event.getSceneY() - anchorY);
            succes = true;
        }

        return succes;
    }

    /**
     * Handler when releasing the target node. Commit position of the node to LayoutX & Y
     * <br/>Check boundaries but not conditions
     * @param event mouseEvent
     */
    public boolean releaseHandler(MouseEvent event){
        boolean succes = false;
            //commit changes to LayoutX and LayoutY
        if(this.releaseBoundary != null){
            Bounds bounds = target.getBoundsInParent();
            if(this.releaseBoundary.contains(bounds)){
                Parent parent = this.target.getParent();
                Point2D p = parent.sceneToLocal(event.getSceneX(), event.getSceneY());
                Point2D oldPos = getCurrentPos(target);

                target.relocate(p.getX() - mouseOffsetFromNodeZeroX-1,p.getY() - mouseOffsetFromNodeZeroY-1);
                Bounds newBounds = target.getBoundsInParent();
                if( ! this.releaseBoundary.contains(newBounds)){
                    target.relocate(oldPos.getX()-1, oldPos.getY()-1);
                } else {
                    succes = true;
                }
            }
        } else {
            Parent parent = this.target.getParent();
            Point2D p = parent.sceneToLocal(event.getSceneX(), event.getSceneY());
            target.relocate(p.getX() - mouseOffsetFromNodeZeroX-1,p.getY() - mouseOffsetFromNodeZeroY-1);
            succes = true;
        }

        //target.relocate(event.getSceneX() - mouseOffsetFromNodeZeroX,event.getSceneY() - mouseOffsetFromNodeZeroY);
        //clear changes from TranslateX and TranslateY
        target.setTranslateX(0);
        target.setTranslateY(0);
        cycleStatus = INACTIVE;

        return succes;
    }

    /*---------------------------Draggable property-----------------------*/

    /**
     * Bind and unbind the eventHandlers depending on the state of the draggable propertie.
     * <br/> Bind a listener to isDraggable to automaticaly bind and unbind in case of a change
     */
    private void createDraggableProperty() {
        isDraggable = new SimpleBooleanProperty();
        isDraggable.addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                target.addEventFilter(MouseEvent.MOUSE_PRESSED, setAnchor);
                target.addEventFilter(MouseEvent.MOUSE_DRAGGED, updatePositionOnDrag);
                target.addEventFilter(MouseEvent.MOUSE_RELEASED, commitPositionOnRelease);
            } else {
                target.removeEventFilter(MouseEvent.MOUSE_PRESSED, setAnchor);
                target.removeEventFilter(MouseEvent.MOUSE_DRAGGED, updatePositionOnDrag);
                target.removeEventFilter(MouseEvent.MOUSE_RELEASED, commitPositionOnRelease);
            }
        });
    }

    /**
     * Check if the target node is set to be draggable
     * @return boolean
     */
    public boolean isIsDraggable() {
        return isDraggable.get();
    }

    /**
     * Return the boolean property checking if the target node can be dragged.
     * Modification add or remove event handlers on the target node.
     * <br/> To end draggable state, set return object to false
     * <br/> Bind example : <i>dragController.isDraggableProperty().bind(isDraggableBox.selectedProperty());</i>
     * @return BooleanProperty. Can be set or binded to other properties
     */
    public BooleanProperty getDraggableProperty() {
        return isDraggable;
    }

    /**
     * Return the node managed by the DragController
     * @return the target node
     */
    public Node getManagedNode(){
        return this.target;
    }

    /*----------------------------------Boundaries----------------------*/

    /**
     * Define the boundaries where the node can be dragged to and released into.
     * The nodes bounds must be contained entirely by the boundaries to do the action
     * (If outsite of parent bounds, no change will be seen)
     * <br/>If only dragBoundary is defined, releaseBoundary will be considered to be equal to dragBoundary
     * <br/>All boundaries must be from the target node s parent coordinate system.
     * <br/><i>Help : Boundaries inside a layout will only be computed after the scene was shown.</i>
     * @param dragBoundary Clip the position of the node inside the boundary
     * @param releaseBoundary Allow release of the node only inside boundary
     */
    public void setBoundaries(Bounds dragBoundary, Bounds releaseBoundary){
        this.dragBoundary = dragBoundary;
        this.releaseBoundary = releaseBoundary;
        if(this.releaseBoundary == null){
            this.releaseBoundary = this.dragBoundary;
        }
    }

    /**
     * Define the boundaries where the node can be dragged to and released into.
     * The nodes bounds must be contained entirely by the boundaries to do the action
     * (If outsite of parent bounds, no change will be seen)
     * <br/>All boundaries must be from the target node s parent coordinate system.
     * <br/><i>Help : Boundaries inside a layout will only be computed after the scene was shown.</i>
     * @param moveBoundary Clip the position of the node inside the boundary
     */
    public void setBoundaries(Bounds moveBoundary){
        setBoundaries(moveBoundary, null);
    }

    /**
     * Get the boundary where the node can be dragged to.
     * <br/>null by default
     * @return the Bounds
     */
    public Bounds getDragBoundary(){
        return this.dragBoundary;
    }

    /**
     * Get the boundary where the node can be released into.
     * <br/>null by default
     * @return the Bounds
     */
    public Bounds getReleaseBoundary() {
        return releaseBoundary;
    }


    /*--------------------------Position------------------------*/

    /**
     * Calculate the coordinate in the layout where the node is drawn in its parent,
     * with layoutX & Y and translateX & Y
     * <br/><a href="https://docs.oracle.com/javase/8/javafx/api/javafx/scene/Node.html#translateXProperty--">Source of calculation</a>
     * @param node the node which position is calculated
     * @return the position of the node in the layout
     */
    public Point2D getCurrentPos(Node node){
        return new Point2D(node.getLayoutX()+node.getTranslateX(), node.getLayoutY()+node.getTranslateY());
    }

    /**
     * Snap the target node back to its last position by setting translateX & Y to 0
     * and stop the drag movement
     */
    public void resetDrag(){
        cycleStatus = INACTIVE;
        target.setTranslateX(0);
        target.setTranslateY(0);
    }


    /**
     * Set the original position of mouse <b>in the scene</b> when grabbing the target node.
     * Used to translate the node.<hr>
     * <p>Nodes layoutX & Y must correspond.
     * If there is the need to place the node at position parentX,parentY in its parent, and the anchor at position anchorInParentX, anchorInParentY in its parents use :
     * <pre>
     * node.relocate(parentX,parentY);
     * Parent parent = node.getParent();
     * Point2D anchorInScene = parent.localToScene(anchorInParentX, anchorInParentY);
     * </pre>
     * </p>
     * <p>
     *     <br/>Carefull management is needed to avoid breaking the class. Use with MouseOffsetFromNode
     *     <br/><i><b>Do not use while the dragging property is activated</b></i>
     * </p>
     * @param anchor the anchor point. Ideally inside the node since grabbing motion is done from within the node
     * @see DragController#setMouseOffsetFromNode(Point2D)
     * @see DragController#grabHandler(MouseEvent)
     * @see DragController#dragHandler(MouseEvent)
     * @see DragController#releaseHandler(MouseEvent)
     */
    public void setAnchors(/*@NotNull*/ Point2D anchor){
        this.anchorX = anchor.getX();
        this.anchorY = anchor.getY();
        target.getLayoutY();
    }

    /**
     * The distance between the position of the mouse and the position of the node
     * when the node is grabbed
     * <br/>Must be the same as distance between the position of the node and the anchor.
     * <hr><b>If node is already positionned in its parent with relocate():</b>
     * <br/>If anchor is at point anchorParentX, anchorParentY in the nodes parent,
     * use offset calculated with
     * <pre>
     *     Point2D posInParent = dragController.getCurrentPos(node);
     *     offsetX = anchorParentX - posInParent.getX();
     *     offsetY = anchorParentY - posInParent.getY();
     * </pre>To find anchorInScene from offset use
     * <pre>
     *     Point2D posInParent = dragController.getCurrentPos(node);
     *     anchorInParentX = posInParent.getX() + offset.getX();
     *     anchorInParentY = posInParent.getY() + offset.getY();
     *     Parent parent = node.getParent();
     *     Point2D anchorInScene = parent.localToScene(anchorInParentX, anchorInParentY);
     * </pre>
     * <p>
     *     <br/>Carefull management is needed to avoid breaking the class. Use with setAnchors
     *     <br/><i><b>Do not use while the dragging property is activated</b></i>
     * </p>
     * @param mouseOffsetFromNode distance between the mouse and the node
     * @see DragController#setAnchors(Point2D)
     * @see DragController#getCurrentPos(Node)
     * @see DragController#grabHandler(MouseEvent)
     * @see DragController#dragHandler(MouseEvent)
     * @see DragController#releaseHandler(MouseEvent)
     */
    public void setMouseOffsetFromNode(Point2D mouseOffsetFromNode) {
        this.mouseOffsetFromNodeZeroX = mouseOffsetFromNode.getX();
        this.mouseOffsetFromNodeZeroY = mouseOffsetFromNode.getY();
    }


    /**
     * Set the translate parameter of the node to the position indicated <b>in the scene</b> (with offset respected).
     * <br/>Doesnt take boundaries or checkers into consideration.
     * @param posInScene does the drag movement respect the dragBoundary ?
     */
    public boolean dragNodeToPos(Point2D posInScene){
        return dragNodeToPos(posInScene, false);
    }

    /**
     * Set the translate parameter of the node to the position indicated <b>in the scene</b> (with offset respected).
     * @param posInScene the position of the mouse in the scene
     * @param respectBoundary does the drag movement respect the dragBoundary ?
     * @see DragController#getDragBoundary()
     */
    public boolean dragNodeToPos(Point2D posInScene, boolean respectBoundary){
        boolean succes = false;
        if (respectBoundary) {
            if(this.dragBoundary != null){
                double oldTranslateX = target.getTranslateX();
                double oldTranslateY = target.getTranslateY();
                target.setTranslateX(posInScene.getX() - anchorX);
                target.setTranslateY(posInScene.getY() - anchorY);
                Bounds bounds = target.getBoundsInParent();
                if(! this.dragBoundary.contains(bounds)){
                    target.setTranslateX(oldTranslateX);
                    target.setTranslateY(oldTranslateY);
                } else {
                    succes = true;
                }
            } else {
                target.setTranslateX(posInScene.getX() - anchorX);
                target.setTranslateY(posInScene.getY() - anchorY);
                succes = true;
            }
        } else {
            target.setTranslateX(posInScene.getX() - anchorX);
            target.setTranslateY(posInScene.getY() - anchorY);
            succes = true;
        }
        return succes;
    }

    /*------------------Checker-------------------------*/

    /**
     * Check condition of grab action on the target node
     * @param grabChecker the condition
     */
    public void setGrabChecker(SimpleChecker grabChecker){
        this.grabChecker = grabChecker;
    }

    /**
     * Check condition of drag action on the target node
     * @param dragChecker the condition
     */
    public void setDragChecker(SimpleChecker dragChecker) {
        this.dragChecker = dragChecker;
    }

    /**
     * Check condition of release action on the target node
     * @param releaseChecker the condition
     */
    public void setReleaseChecker(SimpleChecker releaseChecker) {
        this.releaseChecker = releaseChecker;
    }

    /**
     * Check conditions of grab, drag and release actions on the target node
     * @param grabChecker condition in the check method. Can be null
     * @param dragChecker condition in the check method. Can be null
     * @param releaseChecker condition in the check method. Can be null
     */
    public void setCheckers(SimpleChecker grabChecker, SimpleChecker dragChecker, SimpleChecker releaseChecker){
        this.grabChecker = grabChecker;
        this.dragChecker = dragChecker;
        this.releaseChecker = releaseChecker;
    }

    /**
     * Return the condition of the grab action on the target node
     * @return condition as a SimpleChecker
     * @see SimpleChecker
     */
    public SimpleChecker getGrabChecker() {
        return grabChecker;
    }

    /**
     * Return the condition of the drag action on the target node
     * @return condition as a SimpleChecker
     * @see SimpleChecker
     */
    public SimpleChecker getDragChecker() {
        return dragChecker;
    }

    /**
     * Return the condition of the release action on the target node
     * @return condition as a SimpleChecker
     * @see SimpleChecker
     */
    public SimpleChecker getReleaseChecker() {
        return releaseChecker;
    }

    /**
     * A class to do a check on a condition.
     * <br/>Useful to give a check order from an external class
     */
    public abstract static class SimpleChecker{

        /**Create a simple checker to do a check on a condition.**/
        public SimpleChecker(){

        }

        /**
         * Check the condition of the SimpleChecker.
         * <br/>The condition must be defined in this function
         * @return the result of the check
         */
        public abstract boolean check();
    }
}