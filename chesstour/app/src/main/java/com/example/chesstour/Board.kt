package com.example.chesstour

import kotlin.math.absoluteValue

class Board(dimension: Int) {

    private enum class Cell {
        EMPTY, PIECE, OPTION
    }

    private var grid: Array<Array<Cell>>

    init {
        grid = Array(dimension) { Array(dimension) { Cell.EMPTY } }
    }

    fun isEmpty(x: Int, y: Int) = grid[x][y] == Cell.EMPTY

    fun isPiece(x: Int, y: Int) = grid[x][y] == Cell.PIECE

    fun isOption(x: Int, y: Int) = grid[x][y] == Cell.OPTION

    fun setEmpty(x: Int, y: Int) {
        grid[x][y] = Cell.EMPTY
    }

    fun setPiece(x: Int, y: Int) {
        grid[x][y] = Cell.PIECE
    }

    fun setOption(x: Int, y: Int) {
        grid[x][y] = Cell.OPTION
    }

    fun getOptions(x: Int, y: Int): MutableList<Pair<Int, Int>> {
        val possibleOptions = listOf(
            Pair(1, 2), Pair(1, -2), Pair(-1, 2), Pair(-1, -2),
            Pair(2, 1), Pair(2, -1), Pair(-2, 1), Pair(-2, -1)
        )

        val options = mutableListOf<Pair<Int, Int>>()

        possibleOptions.forEach {
            if (isValidOption(it.first + x, it.second + y)) {
                options.add(Pair(it.first + x, it.second + y))
            }
        }
        return options
    }


    private fun isValidOption(x: Int, y: Int) =
        isInsideBoardCell(x, y) && isEmpty(x, y)

    private fun isInsideBoardCell(x: Int, y: Int) =
        x in 0..7 && y in 0..7

    fun isBlackCell(x: Int, y: Int) = (x + y) % 2 == 0

    fun isValidMove(x: Int, y: Int, moveX: Int, moveY: Int): Boolean {
        val difX = (moveX - x).absoluteValue
        val difY = (moveY - y).absoluteValue

        return ((difX == 2 && difY == 1) || (difX == 1 && difY == 2)) && !isPiece(moveX, moveY)
    }

}