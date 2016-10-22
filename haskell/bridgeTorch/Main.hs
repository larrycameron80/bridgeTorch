module Main where

import Control.Monad
import Data.List

main :: IO ()
main =
    do let initialState = State [Person "A" 1, Person "B" 2, Person "C" 5, Person "D" 8] [] LeftSide 0
       let minTime = minTimeInMinutes initialState
       putStrLn $ show minTime

data TorchSide = LeftSide | RightSide deriving Show
data Person = Person { id :: String, timeToCrossInMinutes :: Int } deriving (Show, Eq)
data State = State { leftSide :: [Person], rightSide :: [Person], torchSide :: TorchSide, elapsedTimeInMinutes :: Int } deriving Show

nextPossibleStates :: State -> [State]
nextPossibleStates state @ (State _ _ LeftSide _) =
    let possiblePersonsToCross = possiblePersonsToCrossFromLeft state
        nextPossibleStates = map (nextStateFromLeft state) possiblePersonsToCross
    in nextPossibleStates
nextPossibleStates state @ (State _ _ RightSide _) =
    let possiblePersonToCross = possiblePersonsToCrossFromRight state
        nextPossibleState = map (nextStateFromRight state) possiblePersonToCross
    in nextPossibleState

possiblePersonsToCrossFromLeft :: State -> [[Person]]
possiblePersonsToCrossFromLeft (State leftSide _ _ _) =
    do person1 <- leftSide
       person2 <- leftSide
       guard (person1 /= person2)
       return [person1, person2]

possiblePersonsToCrossFromRight :: State -> [Person]
possiblePersonsToCrossFromRight (State _ rightSide _ _) = rightSide

nextStateFromLeft :: State -> [Person] -> State
nextStateFromLeft (State leftSide rightSide torchSide elapsedTimeInMinutes) personsToCross =
    let newLeftSide = filter (\person -> not $ elem person personsToCross) leftSide
        newRightSide = rightSide ++ personsToCross
        newTorchSide = flipTorchSide torchSide
        newElapsedTimeInMinutes = elapsedTimeInMinutes + (maximum $ map timeToCrossInMinutes personsToCross)
    in State newLeftSide newRightSide newTorchSide newElapsedTimeInMinutes

nextStateFromRight :: State -> Person -> State
nextStateFromRight (State leftSide rightSide torchSide elapsedTimeInMinutes) personToCross =
    let newLeftSide = personToCross : leftSide
        newRightSide = filter (\person -> person /= personToCross) rightSide
        newTorchSide = flipTorchSide torchSide
        newElapsedTimeInMinutes = elapsedTimeInMinutes + (timeToCrossInMinutes personToCross)
    in State newLeftSide newRightSide newTorchSide newElapsedTimeInMinutes

flipTorchSide :: TorchSide -> TorchSide
flipTorchSide LeftSide = RightSide
flipTorchSide RightSide = LeftSide

finalStates :: State -> [State]
finalStates initialState = recursiveFinalStates [initialState]
    where recursiveFinalStates :: [State] -> [State]
          recursiveFinalStates states =
            let possibleNextStates = do state <- states
                                        if (isFinalState state) then return state else nextPossibleStates state
            in if (all isFinalState possibleNextStates) then possibleNextStates else recursiveFinalStates possibleNextStates
          isFinalState :: State -> Bool
          isFinalState (State [] _ _ _) = True
          isFinalState _ = False

minTimeInMinutes :: State -> Int
minTimeInMinutes initialState =
    let finals = finalStates initialState
    in minimum $ map elapsedTimeInMinutes finals
