//
//  SubscriptionViewController.h
//  MSW2
//
//  Created by simon on 11/10/2015.
//  Copyright (c) 2015 Score Lab. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "ApiMethod.h"
#import "SubscriptionCell.h"

@interface SubscriptionViewController : UITableViewController

@property (strong, nonatomic) NSMutableArray *pseudo;
@property (strong, nonatomic) NSMutableArray *idRow;
@property (strong, nonatomic) IBOutlet UIScrollView *scrollView;

@end
