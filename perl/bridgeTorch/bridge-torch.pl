#!/usr/bin/perl
use strict;
use warnings;


use Data::Dumper;
use List::Util qw(max min);

my $initialState = {
    leftSide => [
        {
            id => 'A',
            timeToCrossMinutes => 1
        }, {
            id => 'B',
            timeToCrossMinutes => 2
        }, {
            id => 'C',
            timeToCrossMinutes => 5
        }, {
            id => 'D',
            timeToCrossMinutes => 8
        }
    ],
    rightSide => [],
    torchSide => 'LEFT',
    elapsedTimeMinutes => 0
};

my $minTime = minTime([$initialState]);

print "$minTime\n";



sub nextPossibleStates {
    my $state = shift;

    my $torchSide = $state->{torchSide};
    if('LEFT' eq $torchSide) {
        return nextPossibleStatesFromLeft($state);
    } elsif('RIGHT' eq $torchSide) {
        return nextPossibleStatesFromRight($state);
    } else {
        die "Unknown torchSide $torchSide";
    }
}

sub nextPossibleStatesFromLeft {
    my $state = shift;

    my $possiblePersonsToCross = possiblePersonsToCrossFromLeft($state);

    my @nextStates = map {
        my $personsToCross = $_;

        nextStateFromLeft($state, $personsToCross);
    } @$possiblePersonsToCross;

    return \@nextStates;
}

sub nextPossibleStatesFromRight {
    my $state = shift;

    my $possiblePersonsToCross = possiblePersonsToCrossFromRight($state);

    my @nextStates = map {
        my $personsToCross = $_;

        nextStateFromRight($state, $personsToCross);
    } @$possiblePersonsToCross;

    return \@nextStates;
}

sub possiblePersonsToCrossFromLeft {
    my $state = shift;

    my $possiblePersonsToCross = [];

    for my $person1 (@{$state->{leftSide}}) {
        for my $person2 (@{$state->{leftSide}}) {
            my $person1Id = $person1->{id};
            my $person2Id = $person2->{id};

            if($person1Id eq $person2Id) {
                next;
            }

            my $personsToCross = [$person1, $person2];
            push(@$possiblePersonsToCross, $personsToCross);
        }
    }

    return $possiblePersonsToCross;
}

sub possiblePersonsToCrossFromRight {
    my $state = shift;

    my $possiblePersonsToCross = [];

    for my $person (@{$state->{rightSide}}) {
        my $personsToCross = [$person];
        push(@$possiblePersonsToCross, $personsToCross);
    }

    return $possiblePersonsToCross;
}

sub nextStateFromLeft {
    my ($state, $personsToCross) = @_;

    my $leftSide = $state->{leftSide};
    my $nextLeftSide = nextLeftSideFromLeft($leftSide, $personsToCross);
    my $rightSide = $state->{rightSide};
    my $nextRightSide = nextRightSideFromLeft($rightSide, $personsToCross);
    my $torchSide = $state->{torchSide};
    my $nextTorchSide = flipTorchSide($torchSide);
    my $elapsedTimeMinutes = $state->{elapsedTimeMinutes};
    my $nextElapsedTimeMinutes = nextElapsedTimeMinutes($elapsedTimeMinutes, $personsToCross);

    my $nextState = {
        leftSide => $nextLeftSide,
        rightSide => $nextRightSide,
        torchSide => $nextTorchSide,
        elapsedTimeMinutes => $nextElapsedTimeMinutes
    };

    return $nextState;
}

sub nextStateFromRight {
    my ($state, $personsToCross) = @_;

    my $leftSide = $state->{leftSide};
    my $nextLeftSide = nextLeftSideFromRight($leftSide, $personsToCross);
    my $rightSide = $state->{rightSide};
    my $nextRightSide = nextRightSideFromRight($rightSide, $personsToCross);
    my $torchSide = $state->{torchSide};
    my $nextTorchSide = flipTorchSide($torchSide);
    my $elapsedTimeMinutes = $state->{elapsedTimeMinutes};
    my $nextElapsedTimeMinutes = nextElapsedTimeMinutes($elapsedTimeMinutes, $personsToCross);

    my $nextState = {
        leftSide => $nextLeftSide,
        rightSide => $nextRightSide,
        torchSide => $nextTorchSide,
        elapsedTimeMinutes => $nextElapsedTimeMinutes
    };

    return $nextState;
}

sub nextLeftSideFromLeft {
    my ($leftSide, $personsToCross) = @_;

    my @nextLeftSide = grep {
        my $person = $_;

        my $personId = $person->{id};

        my $keep = 1;
        for my $personToCross (@$personsToCross) {
            my $personToCrossId = $personToCross->{id};
            if($personId eq $personToCrossId) {
                $keep = 0;
                last;
            }
        }

        $keep;
    } @$leftSide;

    return \@nextLeftSide;
}

sub nextLeftSideFromRight {
    my ($leftSide, $personsToCross) = @_;

    my @nextLeftSide = (@$leftSide, @$personsToCross);

    return \@nextLeftSide;
}

sub nextRightSideFromLeft {
    my ($rightSide, $personsToCross) = @_;

    my @nextRightSide = (@$rightSide, @$personsToCross);

    return \@nextRightSide;
}

sub nextRightSideFromRight {
    my ($rightSide, $personsToCross) = @_;

    my @nextRightSide = grep {
        my $person = $_;

        my $personId = $person->{id};

        my $keep = 1;
        for my $personToCross (@$personsToCross) {
            my $personToCrossId = $personToCross->{id};
            if($personId eq $personToCrossId) {
                $keep = 0;
                last;
            }
        }

        $keep
    } @$rightSide;

    return \@nextRightSide;
}

sub flipTorchSide {
    my $torchSide = shift;

    if('LEFT' eq $torchSide) {
        return 'RIGHT';
    } elsif('RIGHT' eq $torchSide) {
        return 'LEFT';
    } else {
        die "Unknown torchSide $torchSide";
    }
}

sub nextElapsedTimeMinutes {
    my ($elapsedTimeMinutes, $personsToCross) = @_;

    my @timesToCrossMinutes = map {$_->{timeToCrossMinutes}} @$personsToCross;
    my $maxTimeToCrossMinutes = max(@timesToCrossMinutes);

    my $nextElapsedTimeMinutes = $elapsedTimeMinutes + $maxTimeToCrossMinutes;
}

sub finalStates {
    my $states = shift;

    my $currentStates = $states;
    while(1) {
        my $allDone = isAllDone($currentStates);
        last if $allDone;

        my @nextStates = map {
            my $state = $_;

            my $nextPossibleStates = nextPossibleStates($state);

            @$nextPossibleStates;
        } @$currentStates;

        $currentStates = \@nextStates;
    }

    return $currentStates;
}

sub finalTimesToCrossMinutes {
    my $states = shift;

    my $finalStates = finalStates($states);

    my @finalTimesToCrossMinutes = map {$_->{elapsedTimeMinutes}} @$finalStates;

    return \@finalTimesToCrossMinutes;
}

sub minTime {
    my $states = shift;

    my $finalTimes = finalTimesToCrossMinutes($states);
    my $minTime = min(@$finalTimes);

    return $minTime;
}

sub isAllDone {
    my $states = shift;

    my $allDone = all(sub {
        my $state = shift;
        return isDone($state);
    }, @$states);

    return $allDone;
}

sub isDone {
    my $state = shift;

    my $leftSide = $state->{leftSide};
    my $leftSideIsEmpty = @$leftSide == 0;

    return $leftSideIsEmpty;
}

sub all {
    my $predicate = shift;
    my @array = @_;

    for my $element (@array) {
        my $predicateEvaluation = $predicate->($element);
        if(!$predicateEvaluation) {
            return 0;
        }
    }

    return 1;
}