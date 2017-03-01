//
//  TabBarViewController.m
//  MSW2
//
//  Created by simon on 09/09/2015.
//  Copyright (c) 2015 Score Lab. All rights reserved.
//

#import "TabBarViewController.h"
#import "SWRevealViewController.h"

@interface TabBarViewController ()

@end

@implementation TabBarViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    
    // Do any additional setup after loading the view.
    _barButton.target = self.revealViewController;
    _barButton.action = @selector(revealToggle:);
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
    // Dispose of any resources that can be recreated.
}

@end
