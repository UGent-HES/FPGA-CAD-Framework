#!/usr/bin/env python

from place_and_route import PlaceCaller
import os

###############
# Set options #
###############

architecture = 'benchmarks/k6_frac_N10_mem32K_40nm.xml'
circuits_folder = 'benchmarks/'
#circuits = 'bgm blob_merge boundtop ch_intrinsics diffeq1 diffeq2 LU32PEEng LU8PEEng mcml mkDelayWorker32B mkPktMerge mkSMAdapter4B or1200 raygentop sha stereovision0 stereovision1 stereovision2 stereovision3'
circuits = 'bgm mcml LU32PEEng LU8PEEng stereovision2'
options = ['--placer', 'td_gp', '--criticality_exponent', '4']
results_file = 'place.csv'
num_random_seeds = 4

#####################
# Place all ciruits #
#####################
os.chdir('..')
place_caller = PlaceCaller(architecture, circuits_folder, circuits)
place_caller.place_all(options, num_random_seeds)
place_caller.save_results(results_file)