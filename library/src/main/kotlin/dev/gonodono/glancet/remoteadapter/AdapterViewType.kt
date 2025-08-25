package dev.gonodono.glancet.remoteadapter

/**
 * Category types for the two distinct [AdapterView][android.widget.AdapterView]
 * implementations.
 *
 * These aren't used anywhere in the library currently. They've been retained as
 * general information.
 */
public sealed interface AdapterViewType {

    public sealed interface AbsListView : AdapterViewType
    public data object ListView : AbsListView
    public data object GridView : AbsListView

    public sealed interface AdapterViewAnimator : AdapterViewType
    public data object StackView : AdapterViewAnimator
    public data object AdapterViewFlipper : AdapterViewAnimator
}